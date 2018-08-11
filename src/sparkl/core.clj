(ns sparkl.core
  (:require [quil.core :as q :include-macros true]
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

  ;; Angles
(def Ax (atom (Math/toRadians 15)))
(def Ay (atom (Math/toRadians -15)))
(def Az (atom (Math/toRadians 90)))

(defn set-angle
  "Rotates virtual space along y axis if true at given rpm per framrate."
  [angle rpm framerate]
  (if (< @angle 400)
    (swap! angle + (/ (/ (* rpm 400.0) 60) framerate))
    (swap! angle zero)))

(defn screenH [x y z ax ay az h0]
  (Math/abs (Math/round (+ (* x (Math/cos ax)) (* y (Math/cos ay)) (* z (Math/cos az)) h0))))

(defn screenV [x y z ax ay az v0]
  (Math/abs (Math/round (+ (* x (Math/sin ax)) (* y (Math/sin ay)) (* z (Math/sin az)) v0))))

(defn circle
  [x y r]
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
  (let [h0 256 v0 256]
    (map #(let [[x y z] %]
            [(screenH x y z @Ax @Ay @Az h0) (screenV x y z @Ax @Ay @Az v0)]) points)))

(defn setup []
  (q/frame-rate framerate)                    ;; Set framerate to 30 FPS
  (q/background 0 0 0))                 ;; Set the background color to
                                      ;; a nice shade of black.
(defn draw []
  (q/stroke (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill (q/random 255))               ;; Set the fill colour to a random grey
  (let [white (q/color 255 255 255)
        r 100
        graph (project (circle 0 0 r))]
    (q/clear)
    (q/set-pixel 256 256 (q/color 0 255 0))
    (set-angle Ax 0.1 framerate)
    (doseq [[x y] graph]
      (q/set-pixel x y white))))         ;; Draw a point at the center of the screen

(q/defsketch example                  ;; Define a new sketch named example
             :title "Random circles."    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup setup                        ;; Specify the setup fn
             :draw draw                          ;; Specify the draw fn
             :size [512 512])                    ;; You struggle to beat the golden ratio

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prnt/pprint "Rendering..."))
