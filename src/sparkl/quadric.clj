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
;;   up/down arrows   change the speed by 1 rpm (hold shift for 10)
;;   tab              reverses the direction of rotation
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
(def framerate 30)                          ; target frames per second; affects smoothness, not rotation speed
(def start-rpm 4)                           ; starting speed in revolutions per minute (4 = one turn every 15 s)
(def max-rpm 60)                            ; fastest speed the keys allow; beyond this the point cloud strobes
(def tau (* 2 Math/PI))                     ; one full revolution, in radians
(def max-step-ms 100)                       ; the longest time step a single frame may advance
(def frame-count 300)                       ; the number of frames to render for a video (spaced 1/framerate s apart)
(def xyz-length 128)                        ; length of the axes
(def sheet-size 300)                        ; set range from +/- for xy values
(def render-frames false)                   ; set to true to write frames to disk for a video

;; runtime state (changed with the keyboard while running)
(def axis? (atom true))                     ; render xyz axes
(def animated? (atom (:animated (config)))) ; rotate the point cloud each frame
(def counter (atom 0))
(def orient (atom (Math/toRadians 150)))
(def rpm (atom start-rpm))                  ; speed in revolutions per minute, 0 to max-rpm
(def direction (atom 1))                    ; 1 forward, -1 reversed
(def last-millis (atom nil))                ; clock reading at the previous frame

(defn copy-sign [val provider]
  "Return val with sign of provider"
  (* (/ provider (Math/abs provider) val)))

(defn advance
  "Return angle (radians) turned at rpm revolutions per minute for dt-ms
   milliseconds, wrapped into [0, tau)."
  [angle dt-ms rpm]
  (mod (+ angle (* tau rpm (/ dt-ms 60000.0))) tau))

(defn adjust-rpm
  "Return rpm changed by delta, kept within 0 to max-rpm."
  [rpm delta]
  (-> (+ rpm delta) (max 0) (min max-rpm)))

(defn speed-delta
  "Return the rpm step for key k (:up or :down), 10 times larger with shift,
   or nil for any other key."
  [k shift?]
  (when-let [step ({:up 1 :down -1} k)]
    (if shift? (* 10 step) step)))

(defn frame-step
  "Return the milliseconds elapsed from prev-ms to now-ms, at most cap-ms.
   Returns 0 when there is no previous reading or the clock went backwards."
  [prev-ms now-ms cap-ms]
  (if (nil? prev-ms)
    0
    (max 0 (min cap-ms (- now-ms prev-ms)))))

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
  "Handle a key press: 1-6 and left/right select surfaces, up/down change the
   speed (shift for 10), tab reverses, space pauses, a toggles axes."
  []
  (let [k (q/key-as-keyword)
        step (speed-delta k (contains? (q/key-modifiers) :shift))]
    (cond
      (contains? surface-keys k) (select-surface! (surface-keys k))
      step                       (swap! rpm adjust-rpm step)
      ;; Quil has no keyword for tab, so match the raw character
      (= (q/raw-key) \tab)       (swap! direction -)
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
               "   " @rpm " rpm"
               (if (neg? @direction) " reversed" "")
               (if @animated? "" "  (paused)")
               "      1-6 / left-right: surface   up-down: speed (shift x10)   tab: reverse"
               "   space: pause   a: axes   esc: quit")
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
      ;; video: a fixed step per saved frame, however long saving takes
      (if (< @counter frame-count)
        (do
          (swap! counter inc)
          (swap! orient advance (/ 1000.0 framerate) (* @direction @rpm))
          (q/save (str "resources/seq4-" @counter ".png"))))
      ;; live: advance by the time since the last frame. The clock is read even
      ;; while paused, so resuming doesn't jump ahead by the length of the pause.
      (let [now (q/millis)
            dt (frame-step @last-millis now max-step-ms)]
        (reset! last-millis now)
        (when @animated?
          (swap! orient advance dt (* @direction @rpm)))))))
