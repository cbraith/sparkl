:grid-y;; ============================================================================
;; Quadric Surfaces
;; ============================================================================
(ns sparkl.surfaces
  (:require [quil.core :as q]
            [sparkl.styling :as lnf]))

(defn sqr [n]
  "Return square of the provided number."
  (* n n))

(defn paraboloid [a b c [x y]]
  "Define a parabaloid."
  (/ (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) c))

(defn saddle [a b c [x y]]
  "Define a hyperbolic parabaloid."
  (/ (- (/ (sqr x) (sqr b)) (/ (sqr y) (sqr a))) c))

(defn cone [a b c [x y]]
  "Define a cone."
  (Math/sqrt (* (sqr c) (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))))))

(defn one-sheet [a b c [x y]]
  "Define a hyperboloid of one sheet."
  (Math/sqrt (* (sqr c) (- (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) 1.0))))

(defn two-sheet [a b c [x y]]
  "Define a hyperboloid of two sheets."
  (Math/sqrt (* (sqr c) (- (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)) 1.0))))

(defn ellipsoid [a b c [x y]]
  "Define an ellipsoid."
  (Math/sqrt (* (sqr c) (- 1.0 (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)))))))

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

; 1.35 right half of screen
(def settings {:paraboloid {:function paraboloid
                            :origin [(/ (q/screen-width) 1.35) (/ (q/screen-height) 1.2)]
                            :angles [15 -15 270]
                            :constants [15.0 15.0 1.0]
                            :grid-x 12
                            :grid-y 4
                            :mirror false
                            :fore-color lnf/persimmon
                            :aft-color lnf/sriracha
                            :animated true}

               :saddle      {:function saddle
                             :origin [(/ (q/screen-width) 1.35) (/ (q/screen-height) 3)]
                             :angles [0 0 270]
                             :constants [4.0 8.0 4.0]
                             :grid-x 20
                             :grid-y 2
                             :mirror false
                             :fore-color lnf/snow-day
                             :aft-color lnf/umami
                             :animated false}

               :cone        {:function cone
                             :origin [(/ (q/screen-width) 1.35) (/ (q/screen-height) 2)]
                             :angles [15 -15 270]
                             :constants [6.0 6.0 9.0]
                             :grid-x 20
                             :grid-y 4
                             :mirror true
                             :fore-color lnf/wasabi
                             :aft-color lnf/nebula
                             :animated true}

               :two-sheet    {:function two-sheet
                              :origin [(/ (q/screen-width) 1.35) (/ (q/screen-height) 2)]
                              :angles [10 -10 270]
                              :constants [7.0 7.0 7.0]
                              :grid-x 24
                              :grid-y 4
                              :mirror true
                              :fore-color lnf/laughter
                              :aft-color lnf/persimmon
                              :animated false}

               :ellipsoid    {:function ellipsoid
                              :origin [(/ (q/screen-width) 1.35) (/ (q/screen-height) 2)]
                              :angles [30 -30 270]
                              :constants [300.0 300.0 300.0]
                              :grid-x 2
                              :grid-y 40
                              :mirror true
                              :fore-color lnf/grape
                              :aft-color lnf/laughter
                              :animated true}})
