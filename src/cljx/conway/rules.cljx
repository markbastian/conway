(ns conway.rules)

(defn gen-cell[](if (> (Math/random) 0.7) :alive :dead))

(defn seed-grid [rows cols]
  (vec (take rows (repeatedly (fn [] (vec (take cols (repeatedly gen-cell))))))))

(defn neighbors [[i j]]
  (let [x ((juxt inc inc identity dec dec dec identity inc) i)
        y ((juxt identity inc inc inc identity dec dec dec) j)]
    (map vector x y)))

(defn count-neighbors [grid coord]
  (let [n (map #(get-in grid %) (neighbors coord))]
    (count (filter #(= % :alive) n))))

(defn sim-step [grid coord]
  (let [n-live (count-neighbors grid coord)]
    (if (= :alive (get-in grid coord))
      (case n-live
        (2 3) :alive
        :dead)
      (if (= 3 n-live) :alive :dead))))

(defn step [grid]
  (into [] (for [i (range (count grid))]
          (into [] (for [j (range (count (get grid i)))]
                  (sim-step grid [i j]))))))

;(defn step-j [grid i]
;  (loop [j 0 g (transient [])]
;    (if (< j (count (get grid i)))
;      (recur (inc j) (conj! g (sim-step grid [i j])))
;      (persistent! g))))
;
;(defn step [grid]
;  (loop [i 0 v (transient [])]
;    (if (< i (count grid))
;      (recur (inc i) (conj! v (step-j grid i)))
;      (persistent! v))))