(ns cljs.nocturne.app.d3-chart.util
  (:require [cljs.nocturne.util.math :as um]))

(defn translate
  [{:keys [x y]}]
  (let [pos-x (if (number? x) x 0)
        pos-y (if (number? y) y 0)]
    (str "translate(" pos-x "," pos-y ")")))

(defn compute-size
  [size]
  (let [margin {:left 40
                :right 40
                :top 40
                :bottom 40}]
    {:margin margin
     :size size
     :inner-size {:width (- (:width size)
                            (:left margin)
                            (:right margin))
                  :height (- (:height size)
                             (:top margin)
                             (:bottom margin))}}))

(defn min-max-value
  [num-data]
  [(um/min-value num-data)
   (um/max-value num-data)])

(defn separate-data
  [m]
  (apply map vector m))

(defn format-data
  [data & ks]
  (let [fns (->> ks
                 (filter #(not (nil? %)))
                 (map (fn [k] #(get-in % k))))
        f (apply juxt fns)]
    (map f data)))
