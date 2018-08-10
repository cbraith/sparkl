(ns sparkl.core
  (:require [quil.core :as q :include-macros true]))

(defn circle
  [r x]
  (Math/sqrt (- (Math/pow r 2) (Math/pow x 2))))

(defn plot
  [x y r c]
  ;; draw a circle
  (let [xs (range r)]
    (map #([[(+ x %) (+ y (circle r %)) c]
            [(+ x %) (- y (circle r %)) c]
            [(- x %) (+ y (circle r %)) c]
            [(- x %) (- y (circle r %)) c]]) xs)))

(defn setup []
  (q/frame-rate 30)                    ;; Set framerate to 1 FPS
  (q/background 0 0 0))                 ;; Set the background colour to
                                      ;; a nice shade of grey.
(defn draw []
  (q/stroke (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill (q/random 255))               ;; Set the fill colour to a random grey

  (let [white (q/color 255 255 255)]
    ; (q/set-pixel 256 256 white)))
    (doseq [y (range 246 267)] (q/set-pixel 256 y white))))         ;; Draw a point at the center of the screen

(q/defsketch example                  ;; Define a new sketch named example
             :title "A solitary point."    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup setup                        ;; Specify the setup fn
             :draw draw                          ;; Specify the draw fn
             :size [512 512])                    ;; You struggle to beat the golden ratio

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hi."))
