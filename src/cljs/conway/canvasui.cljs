(ns conway.canvasui
  (:require [conway.rules :as rules]))

(defn draw-background [canvas ctx]
  (doto ctx
    (-> .-fillStyle (set! "#000000"))
    (.fillRect 0 0 (.-width canvas) (.-height canvas))))

(defn draw-and-update-grid [canvas ctx grid-ref dim]
  (let [{:keys [grid cx cy]} @grid-ref
        dx (/ (.-width canvas) dim) dy (/ (.-height canvas) dim)]
    (do
    (draw-background canvas ctx)
    (-> ctx .-fillStyle (set! "#00FF00"))
    (doseq [i (range cx) j (range cy) :when (grid [i j])]
      (.fillRect ctx (* dx i) (* dy j) dx dy))
    (swap! grid-ref rules/step))))

(set!
  (.-onload js/window)
  (when (and js/document (.-getElementById js/document))
    (let [cells 100
          grid (atom (rules/seed-grid {:cx cells :cy cells}))
          canvas (.getElementById js/document "myCanvas")
          reset-button (.getElementById js/document "reset")
          ctx (.getContext canvas "2d")]
      (do
        (js/setInterval #(draw-and-update-grid canvas ctx grid cells) 10)
        (set! (.-onclick reset-button) #(reset! grid (rules/seed-grid {:cx cells :cy cells})))))))
