;; ============================================================================
;; Quadric Surfaces
;; ============================================================================
(ns sparkl.surfaces
  (:require [quil.core :as q]
            [sparkl.styling :as lnf]))

(def screen-center 2)
(def screen-right 1.35)

(defn sqr [n]
  "Return square of the provided number."
  (* n n))

(defn paraboloid [a b c [x y]]
  "Define a parabaloid."
  (/ (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) c))

(defn saddle [a b c [x y]]
  "Define a hyperbolic parabaloid."
  (- (/ (sqr x) (sqr b)) (/ (sqr y) (sqr a))))

(defn cone [a b c [x y]]
  "Define a cone."
  (Math/sqrt (* (sqr c) (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))))))

(defn one-sheet [a b c [x y]]
  "Define a hyperboloid of one sheet."
  (Math/sqrt (* (sqr c) (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)) 1.0))))

(defn two-sheet [a b c [x y]]
  "Define a hyperboloid of two sheets."
  (Math/sqrt (* (sqr c) (- (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)) 1.0))))

(defn ellipsoid [a b c [x y]]
  "Define an ellipsoid."
  (Math/sqrt (* (sqr c) (- 1.0 (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)))))))

(defn circle [x y r]
  "Define a circle."

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

(def settings {:paraboloid {:function paraboloid
                            :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 1.2)]
                            :angles [15 -15 270]
                            :constants [15.0 15.0 1.0]
                            :grid-x 4
                            :grid-y 20
                            :mirror false
                            :fore-color lnf/persimmon
                            :aft-color lnf/sriracha
                            :animated true}

               :saddle      {:function saddle
                             :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 3)]
                             :angles [0 0 270]
                             :constants [2.1 18.0 8.0]
                             :grid-x 40
                             :grid-y 1
                             :mirror false
                             :fore-color lnf/snow-day
                             :aft-color lnf/umami
                             :animated false}

               :cone        {:function cone
                             :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 2)]
                             :angles [15 -15 270]
                             :constants [6.0 6.0 9.0]
                             :grid-x 20
                             :grid-y 4
                             :mirror true
                             :fore-color lnf/wasabi
                             :aft-color lnf/umami
                             :animated true}

               :one-sheet    {:function one-sheet
                              :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 2)]
                              :angles [15 -15 270] ; [110 -110 180]
                              :constants [60 60 60.0]
                              :grid-x 40
                              :grid-y 2
                              :mirror true
                              :fore-color lnf/stardust
                              :aft-color lnf/persimmon
                              :animated true}

               :two-sheet    {:function two-sheet
                              :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 2)]
                              :angles [10 -10 270]
                              :constants [7.0 7.0 7.0]
                              :grid-x 24
                              :grid-y 1
                              :mirror true
                              :fore-color lnf/laughter
                              :aft-color lnf/persimmon
                              :animated false}

               :ellipsoid    {:function ellipsoid
                              :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 2)]
                              :angles [30 -30 270]
                              :constants [300.0 300.0 300.0]
                              :grid-x 40
                              :grid-y 2
                              :mirror true
                              :fore-color lnf/snow-day
                              :aft-color lnf/umami
                              :animated true}})
