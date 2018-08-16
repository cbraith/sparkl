(ns sparkl.quadric
  (:require [quil.core :as q]
            [sparkl.surfaces :as s]))

(defn zero [& args]
  0)

(def framerate 30)
(def speed 0.1)

(def axis? false)
(def originX (/ (q/screen-width) 2))
(def originY (/ (q/screen-height) 1.2))
(def xyz-length 128)
(def sheet-size 300)

;; Angles
(def Ax (atom (Math/toRadians 15)))
(def Ay (atom (Math/toRadians -15)))
(def Az (atom (Math/toRadians 270)))
(def orient (atom (Math/toRadians 135)))

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
  (let [step (range (- 0 size) size 20)
        cont (range (- 0 size) size 2)
        xs cont
        ys step
        matrix (for [x xs y ys] [x y])
        ps (reduce into (map #(let [z (surface a b c %)]
                                (if (true? mirror)
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]
                                   [(first %) (second %) (* -1.0 z)] [(* -1 (first %)) (* -1 (second %)) (* -1.0 z)]] ; add mirrored points
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]])) (map #(rotate % @orient) matrix)))]
    ps))

(defn project
  "Project a collection of 3D points onto 2D screen."
  [points]
  (map #(let [[x y z] %]
          [(screenH x y z @Ax @Ay @Az originX) (screenV x y z @Ax @Ay @Az originY) (> y 0)]) points))

(defn setup []
  (q/frame-rate framerate)                    ;; Set framerate to 30 FPS
  (q/background 255 255 255))

(defn draw []
  (q/stroke-weight 1) (let [white (q/color 255 255 255)
                            black (q/color 0 0 0)

                            stardust (q/color 63 137 155)
                            persimmon (q/color 188 62 49)

                            grape (q/color 193 155 179)
                            potato-chip (q/color 195 185 163)

                            ; good
                            wasabi (q/color 150 164 127)
                            nebula (q/color 75 70 67)

                            ; good
                            snow-day (q/color 204 204 204)
                            umami (q/color 102 102 102)

                            fall-foliage (q/color 198 205 194)
                            sriracha (q/color 189 14 26)

                            ; good
                            candlelight (q/color 206 174 71)
                            laughter (q/color 172 87 134)

                            foreground snow-day
                            background umami

                            ; sphere (point-cloud 300.0 300.0 300.0 sheet-size true s/ellipsoid) ; screen-height / 2

                            graph (project (point-cloud 15.0 15.0 1 sheet-size false s/paraboloid))] ; screen-height / 1.2
                            ; graph (project (point-cloud 6 6 9 sheet-size false s/saddle))]
                            ; graph (project (point-cloud 6 6 9 sheet-size true s/cone))] ; screen-height / 2
                            ; graph (project (point-cloud 6 6 10 sheet-size true s/one-sheet))] ; screen-height / 2
                            ; graph (project (point-cloud 6 6 9 sheet-size true s/two-sheet))]
                            ; graph (project sphere)]
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

                        (q/set-pixel originX originY (q/color 0 255 0)) ;; Draw a point at the center of the screen
                        (set-angle orient speed framerate)

                        (doseq [[x y neg] graph]
                          (q/set-pixel x y (if (true? neg) background foreground))
                          (q/set-pixel (+ x 1) y (if (true? neg) background foreground))
                          (q/set-pixel (+ x 1) (+ y 1) (if (true? neg) background foreground))
                          (q/set-pixel x (+ y 1) (if (true? neg) background foreground)))))
