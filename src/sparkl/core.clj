(ns sparkl.core
  (:require [quil.core :as q :include-macros true]
            [sparkl.quadric :as quadric]
            [clojure.pprint :as prnt]))

(q/defsketch sample                 ;; Define a new sketch named example
             :title "Quadric Surfaces"    ;; Set the title of the sketch
             :settings #(q/smooth 2)             ;; Turn on anti-aliasing
             :setup quadric/setup                        ;; Specify the setup fn
             :draw quadric/draw                          ;; Specify the draw fn
             :features [:present]
             :renderer :p2d
             :size :fullscreen)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ; (prnt/pprint (map #(rotate % @orient) (point-cloud 10 10 1 sheet-size false s/paraboloid)))
  ; (prnt/pprint (point-cloud 10 10 1 sheet-size false s/paraboloid))
  (println "Rendering..."))
