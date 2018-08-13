(ns sparkl.core
  (:require [quil.core :as q :include-macros true]
            [sparkl.surfaces :as s]
            [clojure.pprint :as prnt]))

(defn zero [& args]
  0)

(def framerate 60)
(def speed 0.2) ; rpm

(def radius (atom 1))
(def inc-radius (atom true))
(defn toggle [b]
  (not b))
(defn set-radius []
  (if (> @radius 100)
    (swap! inc-radius toggle)
    (if (< @radius 1)
      (swap! inc-radius toggle)))

  (if (true? @inc-radius)
    (swap! radius inc)
    (swap! radius dec))
  @radius)

(def axis? true)
(def originX (/ (q/screen-width) 2))
(def originY (/ (q/screen-height) 2))
(def xyz-length 128)
(def sheet-size 300)

;; Angles
(def Ax (atom (Math/toRadians 30)))
(def Ay (atom (Math/toRadians -30)))
(def Az (atom (Math/toRadians 270)))
(def orient (atom (Math/toRadians 0)))

(defn copy-sign [val provider]
  "Return val with sign of provider"
  (* (/ provider (Math/abs provider) val)))

(defn set-angle
  "Rotates virtual space along y axis if true at given rpm per framrate."
  [angle rpm framerate]
  (if (< @angle 400.0)
    (swap! angle + (/ (/ (* rpm 400.0) 60) framerate))
    (swap! angle zero)))

(defn screenH [x y z ax ay az h0]
  (Math/round (+ (* x (Math/cos ax)) (* y (Math/cos ay)) (* z (Math/cos az)) h0)))

(defn screenV [x y z ax ay az v0]
  (Math/round (+ (* x (Math/sin ax)) (* y (Math/sin ay)) (* z (Math/sin az)) v0)))

(defn rotate [[x y] delta]
  (let [h (Math/hypot x y)
        a (Math/acos (/ x h))
        rot (+ a delta)]
    [(* (Math/cos rot) h) (* (Math/sin rot) h)]))

(defn point-cloud
  [a b c size mirror surface]
  (let [rs (range (- 0 size) size 8)
        matrix (for [x rs y rs] [x y])
        ps (reduce into (map #(let [z (surface a b c %)]
                                (if (true? mirror)
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]
                                   [(first %) (second %) (* -1.0 z)] [(* -1 (first %)) (* -1 (second %)) (* -1.0 z)]] ; add mirrored points
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]])) (map #(rotate % @orient) matrix)))]
    ps))

(defn circle
  [x y z r]
  ;; draw a circle
  ;; TODO: parametize z, right now z is 0
  (let [rs (range 0 (+ r 1) 20)
        xs (reduce into (map #(let [delta (Math/round (Math/sqrt (- (Math/pow r 2) (Math/pow % 2))))]
                                [[(+ x %) (+ y delta) 0]
                                 [(+ x %) (- y delta) 0]
                                 [(- x %) (+ y delta) 0]
                                 [(- x %) (- y delta) 0]]) rs))
        ys (reduce into (map #(let [delta (Math/round (Math/sqrt (- (Math/pow r 2) (Math/pow % 2))))]
                                [[(+ x delta) (+ y %) 0]
                                 [(+ x delta) (- y %) 0]
                                 [(- x delta) (+ y %) 0]
                                 [(- x delta) (- y %) 0]]) rs))]
    (set (reduce into [xs ys]))))

(defn project
  "Project a collection of 3D points onto 2D screen."
  [points]
  (map #(let [[x y z] %]
          [(screenH x y z @Ax @Ay @Az originX) (screenV x y z @Ax @Ay @Az originY) (< x 0)]) points))

(defn setup []
  (q/frame-rate framerate)                    ;; Set framerate to 30 FPS
  (q/background 0 0 0))

(defn draw []
  (q/stroke-weight 1) (let [white (q/color 255 255 255)
                            black (q/color 0 0 0)

                            stardust (q/color 63 137 155)
                            persimmon (q/color 188 62 49)
                            grape (q/color 193 155 179)
                            potato-chip (q/color 195 185 163)
                            wasabi (q/color 150 164 127)
                            nebula (q/color 75 70 67)
                            snow-day (q/color 204 204 204)
                            umami (q/color 102 102 102)
                            fall-foliage (q/color 198 205 194)
                            sriracha (q/color 189 14 26)
                            laughter (q/color 172 87 134)
                            candlelight (q/color 206 174 71)

                            foreground stardust
                            background persimmon

                            sphere (point-cloud 300.0 300.0 300.0 sheet-size true s/ellipsoid)

                            ; graph (project (point-cloud 10 10 1 sheet-size false s/paraboloid))]
                            ; graph (project (point-cloud 13 13 13 sheet-size false s/saddle))]
                            ; graph (project (point-cloud 6 6 5 sheet-size true s/cone))]
                            ; graph (project (point-cloud 6 6 5 sheet-size true s/one-sheet))]
        ; graph (project (point-cloud 6 6 5 sheet-size true s/two-sheet))]
                            graph (project sphere)]
                        (q/clear)
    ;; render axis
                        (if (true? axis?)
                          (do
                            (q/stroke snow-day)
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH xyz-length 0 0 @Ax @Ay @Az originX) (screenV xyz-length 0 0 @Ax @Ay @Az originY)) ; x-axis

                            (q/stroke umami)
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH 0 xyz-length 0 @Ax @Ay @Az originX) (screenV 0 xyz-length 0 @Ax @Ay @Az originY)) ; y-axis

                            (q/stroke fall-foliage)
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH 0 0 xyz-length @Ax @Ay @Az originX) (screenV 0 0 xyz-length @Ax @Ay @Az originY)))) ; z-axis

                        (q/set-pixel originX originY (q/color 0 255 0))
                        (set-angle orient speed framerate)
                        (doseq [[x y neg] graph]
                          (q/set-pixel x y (if (true? neg) background foreground)))))         ;; Draw a point at the center of the screen

(q/defsketch quadric                 ;; Define a new sketch named example
             :title "Quadric Surfaces"    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup setup                        ;; Specify the setup fn
             :draw draw                          ;; Specify the draw fn
             :features [:present]
             :size :fullscreen)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ; (prnt/pprint (map #(rotate % @orient) (point-cloud 10 10 1 sheet-size false s/paraboloid)))
  ; (prnt/pprint (point-cloud 10 10 1 sheet-size false s/paraboloid))
  (println "Rendering..."))
