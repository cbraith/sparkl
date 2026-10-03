(ns sparkl.quadric
  (:require [quil.core :as q]
            [sparkl.surfaces :as s]
            [sparkl.styling :as hue]))

;; surfaces
;; ====================================
;;
;; Switch surfaces while the sketch is running:
;;
;;   1 paraboloid   2 saddle      3 cone
;;   4 one-sheet    5 two-sheet   6 ellipsoid
;;
;;   left/right arrows cycle through the surfaces
;;   space            pauses / resumes the rotation
;;   a                toggles the xyz axes
;;   esc              quits
;;
(def surface-order [:paraboloid :saddle :cone :one-sheet :two-sheet :ellipsoid])
(def surface-keys (zipmap (map #(keyword (str %)) (range 1 (inc (count surface-order))))
                          surface-order))
(def current-surface (atom (first surface-order)))   ; the surface shown at startup

(defn config
  "Return the settings (from surfaces.clj) for the surface currently selected."
  []
  (get s/settings @current-surface))

;; graph options
(def framerate 30)                          ; if animation is too choppy try reducing this to 30 or 18.
(def speed 1)                               ; rpm
(def rotation (* 2 (Math/PI)))              ; hacky way to define a single rotation
(def frame-count 300)                       ; the number of frames to render for a video
(def xyz-length 128)                        ; length of the axes
(def sheet-size 300)                        ; set range from +/- for xy values
(def render-frames false)                   ; set to true to write frames to disk for a video

;; runtime state (changed with the keyboard while running)
(def axis? (atom true))                     ; render xyz axes
(def animated? (atom (:animated (config)))) ; rotate the point cloud each frame
(def counter (atom 0))
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

(defn point-cloud
  "Calculate a set of 3D points per given surface function.
   Points where the surface is undefined (z is NaN, e.g. inside the waist of a
   one-sheet hyperboloid or outside an ellipsoid) are dropped."
  [a b c size grid-x grid-y mirror surface]
  (let [gx (range (- 0 size) size grid-x)
        gy (range (- 0 size) size grid-y)
        matrix (for [x gx y gy] [x y])
        ps (reduce into (map #(let [[x y] %
                                    z (double (surface a b c %))]
                                (cond
                                  (Double/isNaN z) []
                                  (true? mirror) [[x y z] [(* -1 x) (* -1 y) z]
                                                  [x y (* -1.0 z)] [(* -1 x) (* -1 y) (* -1.0 z)]] ; add mirrored points
                                  :else [[x y z] [(* -1 x) (* -1 y) z]]))
                             (map #(rotate % @orient) matrix)))]
    ps))

(defn project
  "Project a collection of 3D points onto 2D screen, using the axis angles
   (ax ay az) and screen origin (h0 v0) of the current surface."
  [points ax ay az h0 v0]
  (map #(let [[x y z] %]
          [(screen-h x y z ax ay az h0) (screen-v x y z ax ay az v0) (> y 0)]) points))

;; keyboard
;; ====================================

(defn select-surface!
  "Make surface k (a key of surfaces/settings) the current surface."
  [k]
  (when (contains? s/settings k)
    (reset! current-surface k)
    (reset! animated? (:animated (config)))))

(defn step-surface!
  "Move delta places (+1 / -1) through surface-order, wrapping around."
  [delta]
  (let [i (.indexOf ^java.util.List surface-order @current-surface)
        n (count surface-order)]
    (select-surface! (nth surface-order (mod (+ i delta) n)))))

(defn key-pressed
  "Handle a key press: 1-6 and the arrow keys select surfaces, space pauses, a toggles axes."
  []
  (let [k (q/key-as-keyword)]
    (cond
      (contains? surface-keys k) (select-surface! (surface-keys k))
      (= k :left)                (step-surface! -1)
      (= k :right)               (step-surface! 1)
      (= k :space)               (swap! animated? not)
      (= k :a)                   (swap! axis? not))))

;; drawing
;; ====================================

(defn setup []
  "Setup drawing area."
  (q/frame-rate framerate))

(defn draw-axes
  "Render the xyz axes from the origin."
  [ax ay az h0 v0]
  (doseq [[dx dy dz color] [[xyz-length 0 0 hue/snow-day]       ; x-axis
                            [0 xyz-length 0 hue/umami]          ; y-axis
                            [0 0 xyz-length hue/fall-foliage]]] ; z-axis
    (q/stroke (apply q/color color))
    (q/line (screen-h 0 0 0 ax ay az h0) (screen-v 0 0 0 ax ay az v0)
            (screen-h dx dy dz ax ay az h0) (screen-v dx dy dz ax ay az v0))))

(defn draw-hud
  "Show the current surface and the key bindings in the top-left corner."
  []
  (q/fill (apply q/color hue/snow-day))
  (q/text-size 14)
  (q/text (str (name @current-surface)
               (if @animated? "" "  (paused)")
               "      1-6 / arrows: surface   space: pause   a: axes   esc: quit")
          20 30))

(defn draw []
  "Draw sketch as indicated each frame."
  (let [cfg (config)
        [h0 v0] (:origin cfg)
        [ax ay az] (map #(Math/toRadians %) (:angles cfg))
        [a b c] (:constants cfg)
        foreground (apply q/color (:fore-color cfg))
        background (apply q/color (:aft-color cfg))
        graph (project (point-cloud a b c sheet-size (:grid-x cfg) (:grid-y cfg) (:mirror cfg) (:function cfg))
                       ax ay az h0 v0)]
    (q/stroke-weight 1)
    (q/clear)
    (q/background (apply q/color hue/wisdom))

    ;; render axes
    (when @axis?
      (draw-axes ax ay az h0 v0))

    (q/set-pixel h0 v0 (apply q/color hue/green)) ;; Draw a point at the center of the screen

    (doseq [[x y neg] graph]
      (q/set-pixel x y (if (true? neg) background foreground))
      (q/set-pixel (+ x 1) y (if (true? neg) background foreground))
      (q/set-pixel (+ x 1) (+ y 1) (if (true? neg) background foreground))
      (q/set-pixel x (+ y 1) (if (true? neg) background foreground)))

    (draw-hud)

    (if (true? render-frames)
      (if (< @counter frame-count)
        (do
          (swap! counter inc)
          (set-angle orient speed framerate)
          (q/save (str "resources/seq4-" @counter ".png"))))
      (when @animated?
        (set-angle orient speed framerate)))))
