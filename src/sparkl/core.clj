(ns sparkl.core
  (:require [quil.core :as q :include-macros true]
            [sparkl.surfaces :as s]
            [clojure.pprint :as prnt]))

(defn zero [& args]
  0)

(def framerate 60)

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

(def axis? false)
(def originX 256)
(def originY 256)
(def origin-length 128)
(def sheet-size 250)

  ;; Angles
(def Ax (atom (Math/toRadians 45)))
(def Ay (atom (Math/toRadians -45)))
(def Az (atom (Math/toRadians 270)))

(defn set-angle
  "Rotates virtual space along y axis if true at given rpm per framrate."
  [angle rpm framerate]
  (if (< @angle 400)
    (swap! angle + (/ (/ (* rpm 400.0) 60) framerate))
    (swap! angle zero)))

(defn screenH [x y z ax ay az h0]
  (Math/round (+ (* x (Math/cos ax)) (* y (Math/cos ay)) (* z (Math/cos az)) h0)))

(defn screenV [x y z ax ay az v0]
  (Math/round (+ (* x (Math/sin ax)) (* y (Math/sin ay)) (* z (Math/sin az)) v0)))

(defn point-cloud
  [a b c size surface]
  (let [rs (range (- 0 size) size 8)
        matrix (for [x rs y rs] [x y])
        ps (reduce into (map #(let [z (surface a b c %)]
                                [[(first %) (second %) z]
                                 [(first %) (second %) (* -1 z)]]) matrix))]
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
          [(screenH x y z @Ax @Ay @Az originX) (screenV x y z @Ax @Ay @Az originY)]) points))

(defn setup []
  (q/frame-rate framerate)                    ;; Set framerate to 30 FPS
  (q/background 0 0 0))                 ;; Set the background color to
                                      ;; a nice shade of black.
(defn draw []
  (q/stroke-weight 1)

  (let [white (q/color 255 255 255)
        graph (project (point-cloud 10 10 1 sheet-size s/paraboloid))]
        ; graph (project (point-cloud 10 12 1 sheet-size s/saddle))]
        ; graph (project (point-cloud 6 6 5 sheet-size s/cone))]
        ; graph (project (point-cloud 6 6 5 sheet-size s/one-sheet))]
        ; graph (project (point-cloud 6 6 5 sheet-size s/two-sheet))]
        ; graph (project (point-cloud 70 70 70 sheet-size s/ellipsoid))]
    (q/clear)

    ;; render axis
    (if (true? axis?)
      (do
        (q/stroke (q/color 191 191 191))
        (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                (screenH origin-length 0 0 @Ax @Ay @Az originX) (screenV origin-length 0 0 @Ax @Ay @Az originY)) ; x-axis

        (q/stroke (q/color 127 127 127))
        (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                (screenH 0 origin-length 0 @Ax @Ay @Az originX) (screenV 0 origin-length 0 @Ax @Ay @Az originY)) ; y-axis

        (q/stroke (q/color 63 63 63))
        (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                (screenH 0 0 origin-length @Ax @Ay @Az originX) (screenV 0 0 origin-length @Ax @Ay @Az originY)))) ; z-axis

    (q/set-pixel originX originY (q/color 0 255 0))
    (set-angle Ax 0.1 framerate)
    (set-angle Ay 0.1 framerate)
    (doseq [[x y] graph]
      (q/set-pixel x y white))))         ;; Draw a point at the center of the screen

(q/defsketch quadric                 ;; Define a new sketch named example
             :title "Quadric Surfaces."    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup setup                        ;; Specify the setup fn
             :draw draw                          ;; Specify the draw fn
             :size [512 512])                    ;; You struggle to beat the golden ratio

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ; (prnt/pprint (project (point-cloud 50 50 50 100 s/ellipsoid)))
  (println "Rendering..."))
