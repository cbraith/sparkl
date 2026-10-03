;; ============================================================================
;; Quadric Surfaces
;; ============================================================================
(ns sparkl.surfaces
  (:require [quil.core :as q]
            [sparkl.styling :as lnf]))

(def screen-center 2)
(def screen-right 2)

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
  "Define a hyperboloid of one sheet: x²/a² + y²/b² - z²/c² = 1.
   Undefined (NaN) inside the waist, where x²/a² + y²/b² < 1."
  (Math/sqrt (* (sqr c) (- (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) 1.0))))

(defn two-sheet [a b c [x y]]
  "Define a hyperboloid of two sheets: z²/c² - x²/a² - y²/b² = 1.
   The two sheets open along the z-axis with vertices at z = ±c."
  (Math/sqrt (* (sqr c) (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)) 1.0))))

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
                            :origin [(/ (q/screen-width) screen-right) (/ (q/screen-height) 1.25)]
                            :angles [15 -15 270]
                            :constants [15.0 15.0 1.0]
                            :grid-x 4
                            :grid-y 20
                            :mirror false
                            :fore-color lnf/persimmon
                            :aft-color lnf/sriracha
                            :animated true}

               :saddle      {:function saddle
                             :origin [(/ (q/screen-width) screen-right) (/ (q/screen-height) 2)]
                             :angles [15 -15 270] ; x and y must not share an angle or the rotation collapses into a 2D "breathing" profile
                             :constants [20.0 20.0 1.0] ; z = x²/20² - y²/20², so z stays within ±225 across the sheet (c is unused)
                             :grid-x 2
                             :grid-y 40
                             :mirror false
                             :fore-color lnf/snow-day
                             :aft-color lnf/umami
                             :animated true}

               :cone        {:function cone
                             :origin [(/ (q/screen-width) screen-right) (/ (q/screen-height) 2)]
                             :angles [15 -15 270]
                             :constants [8.0 8.0 9.0]
                             :grid-x 4
                             :grid-y 40 ;4
                             :mirror true
                             :fore-color lnf/wasabi
                             :aft-color lnf/umami
                             :animated true}

               :one-sheet    {:function one-sheet
                              :origin [(/ (q/screen-width) screen-right) (/ (q/screen-height) 2)]
                              :angles [15 -15 270] ; [110 -110 180]
                              :constants [60.0 60.0 60.0] ; waist radius 60, flares to z = ±294 at the sheet edge
                              :grid-x 40 ; 40
                              :grid-y 4
                              :mirror true
                              :fore-color lnf/stardust
                              :aft-color lnf/persimmon
                              :animated true}

               :two-sheet    {:function two-sheet
                              :origin [(/ (q/screen-width) screen-center) (/ (q/screen-height) 2)]
                              :angles [10 -10 270]
                              :constants [80.0 80.0 60.0] ; vertices at z = ±60, reaching z = ±233 at the sheet edge
                              :grid-x 40
                              :grid-y 4
                              :mirror true
                              :fore-color lnf/laughter
                              :aft-color lnf/persimmon
                              :animated true}

               :ellipsoid    {:function ellipsoid
                              :origin [(/ (q/screen-width) screen-right) (/ (q/screen-height) 2)]
                              :angles [30 -30 270]
                              :constants [300.0 300.0 300.0]
                              :grid-x 2 ; 2
                              :grid-y 40 ;40
                              :mirror true
                              :fore-color lnf/snow-day
                              :aft-color lnf/umami
                              :animated true}})
