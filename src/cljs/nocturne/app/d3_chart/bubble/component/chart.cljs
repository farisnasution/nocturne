(ns cljs.nocturne.app.d3-chart.bubble.component.chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use [cljs.nocturne.app.d3-chart.bubble.component.bubbles :only [bubbles]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent bubble-chart
  [{:keys [size style opts ks data]} owner]
  (display-name [_] "bubble-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                formatted-data (adu/format-data data
                                                (:ord-ks ks)
                                                (:num-ks ks)
                                                (:group-ks ks))]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build bubbles
                       {:g {:x (:left margin)
                            :y (:top margin)}
                        :diameter (min (:width inner-size) (:height inner-size))
                        :style style
                        :opts opts
                        :data formatted-data})])))
