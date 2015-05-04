(ns cljs.nocturne.app.d3-chart.donut-pie.component.chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use [cljs.nocturne.app.d3-chart.donut-pie.component.slices :only
         [datafied-slices]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn ->radius
  [size]
  (/ (min (:width size)
          (:height size))
     2))

(defn construct-g-opts
  [inner-size]
  {:x (/ (:width inner-size) 2)
   :y (/ (:height inner-size) 2)})

(defcomponent donut-pie-chart
  [{:keys [size ks style scale opts data]} owner]
  (display-name [_] "donut-pie-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                formatted-data (adu/format-data data
                                                (:ord-ks ks)
                                                (:num-ks ks))
                radius (->radius inner-size)]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build datafied-slices
                       {:g (construct-g-opts inner-size)
                        :colors (:colors style)
                        :scale {:outer radius
                                :inner (:inner scale)}
                        :data formatted-data
                        :opts opts})])))
