(ns sparkl.quadric
  (:require [quil.core :as q]
            [sparkl.surfaces :as s]
            [sparkl.styling :as lnf]))

;; surfaces
;; ====================================
;;
;; options:
;; paraboloid   saddle      cone
;; one-sheet    two-sheet   ellipsoid
;;
(def config (:paraboloid s/settings))

;; graph options
(def framerate 60)                          ; if animation is too choppy try reducing this to 30 or 18.
(def speed 2)                               ; rpm
(def rotation (* 2 (Math/PI)))              ; hacky way to define a single rotation
(def frame-count 600)                       ; the number of frames to render for a video
(def axis? false)                           ; set to true to render xyz axes
(def xyz-length 128)                        ; length of the axes
(def sheet-size 300)                        ; set range from +/- for xy values
(def render-frames false)                   ; set to true to write frames to disk for a video

;; DO NOT CHANGE THESE VALUES: they are set by the surface definitions in surfaces.clj
(def origin-h (first (:origin config)))
(def origin-v (second (:origin config)))
(def grid-x (:grid-x config))
(def grid-y (:grid-y config))
(def counter (atom 0))

;; Angles
(def Ax (atom (Math/toRadians (nth (:angles config) 0))))
(def Ay (atom (Math/toRadians (nth (:angles config) 1))))
(def Az (atom (Math/toRadians (nth (:angles config) 2))))
(def orient (atom (Math/toRadians 150)))

(defn zero [& args]
  0)

(defn copy-sign [val provider]
  "Return val with sign of provider"
  (* (/ provider (Math/abs provider) val)))

(defn set-angle [angle rpm framerate]
  "Rotates virtual space along y axis if true at given rpm per framrate."
  (if (< @angle rotation)
    (swap! angle + (/ (/ (* rpm rotation) 15) framerate))
    (swap! angle zero)))

(defn screen-h [x y z ax ay az h0]
  "Calculate the x projection from 3-space."
  (Math/round (+ (* x (Math/cos ax)) (* y (Math/cos ay)) (* z (Math/cos az)) h0)))

(defn screen-v [x y z ax ay az v0]
  "Calculate the y projection from 3-space."
  (Math/round (+ (* x (Math/sin ax)) (* y (Math/sin ay)) (* z (Math/sin az)) v0)))

(defn rotate [[x y] delta]
  "Calculate coordinates of point (x, y) rotated around the origin by angle delta."
  (let [h (Math/hypot x y)
        a (Math/acos (/ x h))
        rot (+ a delta)]
    [(* (Math/cos rot) h) (* (Math/sin rot) h)]))

(defn point-cloud [a b c size mirror surface]
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

(defn project [points]
  "Project a collection of 3D points onto 2D screen."

  (map #(let [[x y z] %]
          [(screen-h x y z @Ax @Ay @Az origin-h) (screen-v x y z @Ax @Ay @Az origin-v) (> y 0)]) points))

(defn setup []
  "Setup drawing area."
  (q/frame-rate framerate))

(defn draw []
  "Draw sketch as indicated each frame."
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
                        (q/background (apply q/color lnf/wisdom))

                        ;; render axes
                        (if (true? axis?)
                          (do
                            (q/stroke (apply q/color lnf/snow-day))
                            (q/line (screen-h 0 0 0 @Ax @Ay @Az origin-h) (screen-v 0 0 0 @Ax @Ay @Az origin-v)
                                    (screen-h xyz-length 0 0 @Ax @Ay @Az origin-h) (screen-v xyz-length 0 0 @Ax @Ay @Az origin-v)) ; x-axis

                            (q/stroke (apply q/color lnf/umami))
                            (q/line (screen-h 0 0 0 @Ax @Ay @Az origin-h) (screen-v 0 0 0 @Ax @Ay @Az origin-v)
                                    (screen-h 0 xyz-length 0 @Ax @Ay @Az origin-h) (screen-v 0 xyz-length 0 @Ax @Ay @Az origin-v)) ; y-axis

                            (q/stroke (apply q/color lnf/fall-foliage))
                            (q/line (screen-h 0 0 0 @Ax @Ay @Az origin-h) (screen-v 0 0 0 @Ax @Ay @Az origin-v)
                                    (screen-h 0 0 xyz-length @Ax @Ay @Az origin-h) (screen-v 0 0 xyz-length @Ax @Ay @Az origin-v)))) ; z-axis

                        (q/set-pixel origin-h origin-v (apply q/color lnf/green)) ;; Draw a point at the center of the screen

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
