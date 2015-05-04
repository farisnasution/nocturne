(ns cljs.nocturne.app.d3-chart.axis.component.numerical-tick
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.math :as um])
  (:use [cljs.nocturne.app.d3-chart.axis.component.tick :only [tick]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-g
  [scale-fn orient data]
  (let [value (scale-fn data)]
    (cond
     (or (= orient :top)
         (= orient :bottom)) {:x value}
     (or (= orient :left)
         (= orient :right)) {:y value})))

(defcomponent numerical-tick
  [{:keys [scale-fn orient opts data]} owner]
  (display-name [_] "numerical-tick")
  (render [_]
          (om/build tick
                    {:g (construct-g scale-fn orient data)
                     :orient orient
                     :opts opts
                     :data data})))

(defn construct-new-data
  [tick [min-data max-data]]
  (let [upper-tick (if-not (nil? tick)
                     (if (> tick max-data)
                       max-data
                       tick)
                     max-data)
        upper-data (range 0 (inc max-data) upper-tick)
        pos-min-data (um/->pos min-data)
        lower-tick (if-not (nil? tick)
                     (if (> tick pos-min-data)
                       pos-min-data
                       tick)
                     pos-min-data)
        lower-data (when (neg? min-data)
                     (->> (range 0 (inc pos-min-data) lower-tick)
                          next
                          reverse
                          (map #(* -1 %))))
        new-data (concat lower-data upper-data)]
    new-data))

(defcomponent numerical-ticks
  [{:keys [scale-fn orient opts data]} owner]
  (display-name [_] "numerical-ticks")
  (render [_]
          [:g {}
           (om/build-all numerical-tick
                         (->> data
                              (construct-new-data (:tick opts))
                              (map-indexed (fn [idx d]
                                             {:scale-fn scale-fn
                                              :orient orient
                                              :opts opts
                                              :data d
                                              :react-key idx})))
                         {:key :react-key})]))
