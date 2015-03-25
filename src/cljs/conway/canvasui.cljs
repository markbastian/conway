(ns conway.canvasui
  (:require [conway.rules :as rules]))

(defn draw-grid [ctx n dim]
  (doseq [i (range 0 n)]
    (doseq [j (range 0 n)]
      (doto ctx
        (-> .-fillStyle (set! (if (even? (+ i j)) "#FF0000" "#00FF00")))
        (.fillRect (* dim i) (* dim j) dim dim)))))

(defn draw-cell-grid [ctx grid dim]
  (do
    (doseq [i (range (count @grid))]
      (doseq [j (range (count (get @grid i)))]
        (let [c (if (= :alive (get-in @grid [i j])) "#00FF00" "#FF0000")]
          (doto ctx
            (-> .-fillStyle (set! c))
            (.fillRect (* dim i) (* dim j) dim dim)))))
    (swap! grid rules/step)))

(set! (.-onload js/window)
      (when (and js/document (.-getElementById js/document))
        (let [grid (atom (rules/seed-grid 100 100))
              canvas (.getElementById js/document "myCanvas")
              reset-button (.getElementById js/document "reset")
              ctx (.getContext canvas "2d")]
          (do
            (js/setInterval #(draw-cell-grid ctx grid 4) 10)
            (set! (.-onclick reset-button) #(reset! grid (rules/seed-grid 100 100)))))))
