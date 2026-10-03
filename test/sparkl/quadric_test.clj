(ns sparkl.quadric-test
  ;; Require sparkl.quadric, never sparkl.core: loading core opens the sketch window.
  (:require [clojure.test :refer [deftest testing is]]
            [sparkl.quadric :as quadric :refer [advance frame-step tau]]))

(def eps 1e-9)

(defn close?
  "True when angles a and b are equal within eps, treating 0 and tau as the same angle."
  [a b]
  (let [d (Math/abs (double (- a b)))]
    (or (< d eps) (< (Math/abs (- d tau)) eps))))

(deftest advance-rate
  (testing "1 rpm turns a quarter revolution in 15 seconds"
    (is (close? (/ tau 4) (advance 0 15000 1))))
  (testing "rpm scales the rate"
    (is (close? (/ tau 4) (advance 0 7500 2)))
    (is (close? 0 (advance 0 15000 4)) "4 rpm completes a revolution in 15 s")))

(deftest advance-wraps
  (testing "passing a full revolution keeps the remainder"
    (is (close? (/ tau 4) (advance (* 3 (/ tau 4)) 30000 1))))
  (testing "the result stays within [0, tau)"
    (let [a (advance (* 3 (/ tau 4)) 30000 1)]
      (is (<= 0 a))
      (is (< a tau)))))

(deftest advance-zero-step
  (is (= 1.25 (advance 1.25 0 4))))

(deftest advance-is-frame-rate-independent
  (let [start 0.5
        steps (fn [n dt] (reduce (fn [a _] (advance a dt 4)) start (range n)))
        one-second (advance start 1000 4)]
    (is (close? one-second (steps 60 (/ 1000.0 60))) "60 fps")
    (is (close? one-second (steps 30 (/ 1000.0 30))) "30 fps")))

(deftest frame-step-measures-elapsed-time
  (testing "no previous reading means no step, so the first frame doesn't jump"
    (is (= 0 (frame-step nil 4321 100))))
  (testing "an ordinary frame passes through"
    (is (= 16 (frame-step 1000 1016 100))))
  (testing "a long gap is capped"
    (is (= 100 (frame-step 1000 6000 100))))
  (testing "a clock that goes backwards gives no step"
    (is (= 0 (frame-step 1000 900 100)))))
