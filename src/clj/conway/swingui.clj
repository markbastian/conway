(ns conway.swingui
  (:gen-class)
  (:require [conway.rules :as rules])
  (:import (javax.swing JFrame JPanel JButton BoxLayout Box)
           (java.awt BorderLayout Graphics2D Color ImageCapabilities)
           (java.awt.geom Rectangle2D$Double)
           (java.awt.event ActionListener)
           (java.awt.image VolatileImage)))

(defn panel [grid-ref]
  (doto (proxy [JPanel] []
    (paint [g]
      (let [{:keys [grid cx cy]} @grid-ref
            w (.getWidth this)
            h (.getHeight this)
            bg (Rectangle2D$Double. 0 0 w (.getHeight this))
            vimg (.createVolatileImage this w h (ImageCapabilities. true))
            g2d (.createGraphics vimg)]
        (doto g2d
          (.setPaint Color/BLACK)
          (.fill bg))
        (doseq [i (range cx) j (range cy) :when (grid [i j])]
          (let [sq (Rectangle2D$Double. (* i (/ (double w) cx))
                                        (* j (/ (double h) cy))
                                        (/ (double w) cx)
                                        (/ (double h) cy))]
            (do (.setPaint g2d Color/GREEN) (.fill g2d sq))))
        (.drawImage g vimg 0 0 this))))
    (#(add-watch grid-ref :repaint (fn [_ _ _ _] (.repaint %))))))

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
                (actionPerformed [_ _] (reset! grid-ref (rules/seed-grid @grid-ref)))))))))

(defn launch [exit-behavior]
  (let [grid (atom (rules/seed-grid {:cx 200 :cy 200}))]
    (doto (JFrame. "Conway's Game of Life")
      (.setSize 800 600)
      (.setDefaultCloseOperation exit-behavior)
      (.add (panel grid) BorderLayout/CENTER)
      (.add (south-panel grid) BorderLayout/SOUTH)
      (.setVisible true))))

(defn -main [& args]
  (launch JFrame/EXIT_ON_CLOSE))

;(launch JFrame/EXIT_ON_CLOSE)
