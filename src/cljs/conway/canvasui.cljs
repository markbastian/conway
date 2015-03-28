(ns conway.canvasui
  (:require [conway.rules :as rules]))

(defn draw-background [canvas ctx]
  (doto ctx
    (-> .-fillStyle (set! "#000000"))
    (.fillRect 0 0 (.-width canvas) (.-height canvas))))

(defn draw-cell-grid [canvas ctx grid dim]
  (let [dx (/ (.-width canvas) dim)
        dy (/ (.-height canvas) dim)]
    (do
    (draw-background canvas ctx)
    (-> ctx .-fillStyle (set! "#00FF00"))
    (doseq [i (range (count @grid))]
      (doseq [j (range (count (get @grid i)))]
        (when (= :alive (get-in @grid [i j]))
          (.fillRect ctx (* dx i) (* dy j) dx dy))))
    (swap! grid rules/step))))

(set! (.-onload js/window)
      (when (and js/document (.-getElementById js/document))
        (let [cells 50
              grid (atom (rules/seed-grid cells cells))
              canvas (.getElementById js/document "myCanvas")
              reset-button (.getElementById js/document "reset")
              ctx (.getContext canvas "2d")]
          (do
            (js/setInterval #(draw-cell-grid canvas ctx grid cells) 10)
            (set! (.-onclick reset-button) #(reset! grid (rules/seed-grid cells cells)))))))
