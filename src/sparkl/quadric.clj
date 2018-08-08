(ns sparkl.quadric
  (:require [quil.core :as q]
            [sparkl.surfaces :as s]
            [sparkl.styling :as lnf]))

(defn zero [& args]
  0)

;; options
(def framerate 60)
(def speed 2)
(def rotation (* 2 (Math/PI)))
(def counter (atom 0))
(def frame-count 600)
(def axis? false)
(def render-frames false)

;; surface settings
(def config (:cone s/settings))
(def originX (first (:origin config)))
(def originY (second (:origin config)))
(def xyz-length 128)
(def sheet-size 300)
(def grid-x (:grid-x config))
(def grid-y (:grid-y config))

;; Angles
(def Ax (atom (Math/toRadians (nth (:angles config) 0))))
(def Ay (atom (Math/toRadians (nth (:angles config) 1))))
(def Az (atom (Math/toRadians (nth (:angles config) 2))))
(def orient (atom (Math/toRadians 10)))

(defn copy-sign [val provider]
  "Return val with sign of provider"
  (* (/ provider (Math/abs provider) val)))

(defn set-angle
  "Rotates virtual space along y axis if true at given rpm per framrate."
  [angle rpm framerate]
  (if (< @angle rotation)
    (swap! angle + (/ (/ (* rpm rotation) 15) framerate))
    (swap! angle zero)))

(defn screenH [x y z ax ay az h0]
  (Math/round (+ (* x (Math/cos ax)) (* y (Math/cos ay)) (* z (Math/cos az)) h0)))

(defn screenV [x y z ax ay az v0]
  (Math/round (+ (* x (Math/sin ax)) (* y (Math/sin ay)) (* z (Math/sin az)) v0)))

(defn rotate [[x y] delta]
  (let [h (Math/hypot x y)
        a (Math/acos (/ x h))
        rot (+ a delta)]
    [(* (Math/cos rot) h) (* (Math/sin rot) h)]))

(defn point-cloud
  [a b c size mirror surface]
  "Calculate a set of 3D points per given surface function."

  ;; TODO: purify grid-size & grid-step
  (let [gx (range (- 0 size) size grid-x)
        gy (range (- 0 size) size grid-y)
        xs gx
        ys gy
        matrix (for [x xs y ys] [x y])
        ps (reduce into (map #(let [z (surface a b c %)]
                                (if (true? mirror)
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]
                                   [(first %) (second %) (* -1.0 z)] [(* -1 (first %)) (* -1 (second %)) (* -1.0 z)]] ; add mirrored points
                                  [[(first %) (second %) z] [(* -1 (first %)) (* -1 (second %)) z]])) (map #(rotate % @orient) matrix)))]
    ps))

(defn project
  "Project a collection of 3D points onto 2D screen."
  [points]
  (map #(let [[x y z] %]
          [(screenH x y z @Ax @Ay @Az originX) (screenV x y z @Ax @Ay @Az originY) (> y 0)]) points))

(defn setup []
  (q/frame-rate framerate)                    ;; Set framerate to 30 FPS
  (q/background 255 255 255))

(defn draw []
  (q/stroke-weight 1) (let [foreground (apply q/color (:fore-color config))
                            background (apply q/color (:aft-color config))
                            graph (project (point-cloud
                                            (nth (:constants config) 0)
                                            (nth (:constants config) 1)
                                            (nth (:constants config) 2)
                                            sheet-size
                                            (:mirror config)
                                            (:function config)))]
                        (q/clear)

                        ;; render axes
                        (if (true? axis?)
                          (do
                            (q/stroke (apply q/color lnf/snow-day))
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH xyz-length 0 0 @Ax @Ay @Az originX) (screenV xyz-length 0 0 @Ax @Ay @Az originY)) ; x-axis

                            (q/stroke (apply q/color lnf/umami))
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH 0 xyz-length 0 @Ax @Ay @Az originX) (screenV 0 xyz-length 0 @Ax @Ay @Az originY)) ; y-axis

                            (q/stroke (apply q/color lnf/fall-foliage))
                            (q/line (screenH 0 0 0 @Ax @Ay @Az originX) (screenV 0 0 0 @Ax @Ay @Az originY)
                                    (screenH 0 0 xyz-length @Ax @Ay @Az originX) (screenV 0 0 xyz-length @Ax @Ay @Az originY)))) ; z-axis

                        (q/set-pixel originX originY (q/color 0 255 0)) ;; Draw a point at the center of the screen

                        (doseq [[x y neg] graph]
                          (q/set-pixel x y (if (true? neg) background foreground))
                          (q/set-pixel (+ x 1) y (if (true? neg) background foreground))
                          (q/set-pixel (+ x 1) (+ y 1) (if (true? neg) background foreground))
                          (q/set-pixel x (+ y 1) (if (true? neg) background foreground)))

                        (if (true? render-frames)
                          (if (< @counter frame-count)
                            (do
                              (swap! counter inc)
                              (set-angle orient speed framerate)
                              (q/save (str "resources/cone-" @counter ".png"))))
                          (if (:animated config)
                            (set-angle orient speed framerate)))))
