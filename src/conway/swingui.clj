(ns conway.swingui
  (:gen-class)
  (:require [conway.rules])
  (:import (javax.swing JFrame JPanel JButton)
           (java.awt BorderLayout Graphics2D Color ImageCapabilities)
           (java.awt.geom Rectangle2D$Double)
           (java.awt.event ActionListener)
           (java.awt.image VolatileImage)))

(defn panel [grid-ref]
  (let [pnl (proxy [JPanel] []
    (paint [g]
      (let [w (.getWidth this) 
            h (.getHeight this)
            bg (Rectangle2D$Double. 0 0 w (.getHeight this))
            vimg (.createVolatileImage this w h (ImageCapabilities. true))
            g2d (.createGraphics vimg)]
        (doto g2d
          (.setPaint Color/BLACK)
          (.fill bg))
        (doseq [i (range (count @grid-ref))]
          (doseq [j (range (count (get @grid-ref i)))]
            (let [c (if (= :alive (get-in @grid-ref [i j])) Color/GREEN Color/RED)
                  sq (Rectangle2D$Double. (* i (/ w (count @grid-ref)))
                                          (* j (/ h (count (get @grid-ref i))))
                                          (/ w (count @grid-ref))
                                          (/ h (count (get @grid-ref i))))]
              (do
                (.setPaint g2d c)
                (.fill g2d sq)))))
        (.drawImage g vimg 0 0 this)
        )))]
    (do 
      (add-watch grid-ref :repaint (fn [_ _ _ _] (.repaint pnl)))
    pnl)))

(defn sim [grid-ref]
  (future (loop []
    (dosync (ref-set grid-ref (conway.rules/step @grid-ref)))
    (recur))))

(defn button [grid-ref] 
  (doto (JButton. "Go!")
    (.addActionListener 
      (reify ActionListener
        (actionPerformed [_ _] 
          (sim grid-ref)
          ;(dosync (ref-set grid-ref (conway.rules/step @grid-ref)))
          )))))

(defn launch [exit-behavior]
  (let [grid-ref (ref (conway.rules/seed-grid 100 100))]
    (doto (JFrame. "Conway's Game of Life")
      (.setSize 800 600)
      (.setDefaultCloseOperation exit-behavior)
      (.add (panel grid-ref) BorderLayout/CENTER)
      (.add (button grid-ref) BorderLayout/SOUTH)
      (.setVisible true))))

(defn -main [& args]
  (launch JFrame/EXIT_ON_CLOSE))
