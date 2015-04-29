(ns conway.rules
  (:require [clojure.core.reducers :as r]))

(defn seed-grid [{:keys [cx cy] :as m}]
  (into m { :grid (set (for [i (range cx) j (range cy) :when (> (Math/random) 0.7)] [i j]))}))

(defn neighbors [[i j]]
  (let [x ((juxt inc inc identity dec dec dec identity inc) i)
        y ((juxt identity inc inc inc identity dec dec dec) j)]
    (map vector x y)))

(defn live? [{ :keys [grid cx cy]} [i j :as cell] freq]
  (and (<= 0 i (dec cx)) (<= 0 j (dec cy)) (or (= 3 freq) (and (= 2 freq) (grid cell)))))

;(defn step [m]
;  (into m { :grid (set (for [[cell freq] (frequencies (mapcat neighbors (m :grid)))
;                             :when (live? m cell freq)] cell))}))

;(defn step [m]
;(into m { :grid (set (keys (filter (fn [[cell freq]] (live? m cell freq))
;          (frequencies (mapcat neighbors (m :grid))))))}))

;(defn step [m]
;  (into m { :grid (->> (m :grid)
;                       (mapcat neighbors)
;                       frequencies
;                       (filter (fn [[cell freq]] (live? m cell freq)))
;                       keys
;                       set)}))

(defn step [m]
  (update-in m [:grid]
             #(->> %
                   (mapcat neighbors)
                   frequencies
                   (filter (fn [[cell freq]] (live? m cell freq)))
                   keys
                   set)))