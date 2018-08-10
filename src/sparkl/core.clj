(ns sparkl.core
  (:require [quil.core :as q :include-macros true]
            [clojure.pprint :as prnt]))

(defn circle
  [x y r]
  ;; draw a circle
  (let [rs (range (+ r 1))
        xs (reduce into (map #(let [delta (Math/round (Math/sqrt (- (Math/pow r 2) (Math/pow % 2))))]
                                [[(+ x %) (+ y delta)]
                                 [(+ x %) (- y delta)]
                                 [(- x %) (+ y delta)]
                                 [(- x %) (- y delta)]]) rs))
        ys (reduce into (map #(let [delta (Math/round (Math/sqrt (- (Math/pow r 2) (Math/pow % 2))))]
                                [[(+ x delta) (+ y %)]
                                 [(+ x delta) (- y %)]
                                 [(- x delta) (+ y %)]
                                 [(- x delta) (- y %)]]) rs))]
    (set (reduce into [xs ys]))))

(defn setup []
  (q/frame-rate 30)                    ;; Set framerate to 1 FPS
  (q/background 0 0 0))                 ;; Set the background colour to
                                      ;; a nice shade of grey.
(defn draw []
  (q/stroke (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill (q/random 255))               ;; Set the fill colour to a random grey

  (let [white (q/color 255 255 255) graph (circle 256 256 50)]
    (doseq [[x y] graph] (q/set-pixel x y white))))         ;; Draw a point at the center of the screen

(q/defsketch example                  ;; Define a new sketch named example
             :title "A solitary point."    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup setup                        ;; Specify the setup fn
             :draw draw                          ;; Specify the draw fn
             :size [512 512])                    ;; You struggle to beat the golden ratio

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (prnt/pprint (circle 256 256 20)))
