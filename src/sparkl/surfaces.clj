;; ============================================================================
;; Quadric Surfaces
;; ============================================================================
(ns sparkl.surfaces)

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
