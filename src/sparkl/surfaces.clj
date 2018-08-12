;; ============================================================================
;; Quadric Surfaces
;; ============================================================================

(ns sparkl.surfaces)

(defn sqr [n]
  (* n n))

(defn paraboloid [a b c [x y]]
  "Reder a parabaloid."
  (/ (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) c))

(defn saddle [a b c [x y]]
  "Render a hyperbolic parabaloid."
  (/ (- (/ (sqr x) (sqr b)) (/ (sqr y) (sqr a))) c))

(defn cone [a b c [x y]]
  "Render a cone."
  (Math/sqrt (* (sqr c) (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))))))

(defn one-sheet [a b c [x y]]
  "Render a hyperboloid of one sheet."
  (Math/sqrt (* (sqr c) (- (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b))) 1))))

(defn two-sheet [a b c [x y]]
  "Render a hyperboloid of two sheets."
  (Math/sqrt (* (sqr c) (- (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)) 1))))

(defn ellipsoid [a b c [x y]]
  "Render an ellipsoid."
  (Math/sqrt (* (sqr c) (- 1 (+ (/ (sqr x) (sqr a)) (/ (sqr y) (sqr b)))))))
