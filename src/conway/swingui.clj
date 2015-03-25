(ns conway.swingui
  (:gen-class)
  (:require [conway.rules :as rules])
  (:import (javax.swing JFrame JPanel JButton BoxLayout Box)
           (java.awt BorderLayout Graphics2D Color ImageCapabilities)
           (java.awt.geom Rectangle2D$Double)
           (java.awt.event ActionListener)
           (java.awt.image VolatileImage)))

(defn panel [grid]
  (doto (proxy [JPanel] []
    (paint [g]
      (let [w (.getWidth this)
            h (.getHeight this)
            bg (Rectangle2D$Double. 0 0 w (.getHeight this))
            vimg (.createVolatileImage this w h (ImageCapabilities. true))
            g2d (.createGraphics vimg)]
        (doto g2d
          (.setPaint Color/BLACK)
          (.fill bg))
        (doseq [i (range (count @grid))]
          (doseq [j (range (count (get @grid i)))]
            (let [c (if (= :alive (get-in @grid [i j])) Color/GREEN Color/RED)
                  sq (Rectangle2D$Double. (* i (/ w (count @grid)))
                                          (* j (/ h (count (get @grid i))))
                                          (/ w (count @grid))
                                          (/ h (count (get @grid i))))]
              (do (.setPaint g2d c) (.fill g2d sq)))))
        (.drawImage g vimg 0 0 this))))
    (#(add-watch grid :repaint (fn [_ _ _ _] (.repaint %))))))

(defn sim [grid]
  (future (loop []
            (dosync (swap! grid rules/step))
            (recur))))

(defn south-panel [grid-ref]
  (doto (Box. BoxLayout/Y_AXIS)
    (.add (doto (JButton. "Go")
            (.addActionListener
              (reify ActionListener
                (actionPerformed [_ _] (sim grid-ref))))))
    (.add (doto (JButton. "Reset")
            (.addActionListener
              (reify ActionListener
                (actionPerformed [_ _] (reset! grid-ref (rules/seed-grid 100 100)))))))))

(defn launch [exit-behavior]
  (let [grid (atom (rules/seed-grid 100 100))]
    (doto (JFrame. "Conway's Game of Life")
      (.setSize 800 600)
      (.setDefaultCloseOperation exit-behavior)
      (.add (panel grid) BorderLayout/CENTER)
      (.add (south-panel grid) BorderLayout/SOUTH)
      (.setVisible true))))

(defn -main [& args]
  (launch JFrame/EXIT_ON_CLOSE))

;(launch JFrame/DISPOSE_ON_CLOSE)
