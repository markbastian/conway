(ns conway.canvasui
  (:require [conway.rules :as rules]))

(defn draw-grid [ctx n dim]
  (doseq [i (range 0 n)]
    (doseq [j (range 0 n)]
      (doto ctx
        (-> .-fillStyle (set! (if (even? (+ i j)) "#FF0000" "#00FF00")))
        (.fillRect (* dim i) (* dim j) dim dim)))))

(set! (.-onload js/window)
      (when (and js/document (.-getElementById js/document))
        (let [canvas (.getElementById js/document "myCanvas")
              ctx (.getContext canvas "2d")]
          (doto ctx
            (draw-grid 100 10)))))
