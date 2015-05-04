(ns cljs.nocturne.app.d3-chart.bar.component.chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu]
            [cljs.nocturne.app.d3-chart.axis.component.axis :as adaca])
  (:use [cljs.nocturne.app.d3-chart.scale.ordinal :only [ordinal-scale]]
        [cljs.nocturne.app.d3-chart.scale.linear :only [linear-scale]]
        [cljs.nocturne.app.d3-chart.bar.component.vertical-rect :only
         [vertical-rect]]
        [cljs.nocturne.app.d3-chart.bar.component.horizontal-rect :only
         [horizontal-rect]]
        [cljs.nocturne.app.d3-chart.bar.component.rects :only [rects]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-vertical-scale
  [ord-data min-data max-data padding inner-size]
  [(ordinal-scale {:domain ord-data
                   :range-bands [[0 (:width inner-size)] padding]})
   (linear-scale {:domain [min-data max-data]
                  :range-scale [(:height inner-size) 0]})])

(defcomponent vertical-bar-chart
  [{:keys [size scale ks rects-opts x-axis-opts y-axis-opts data]} owner]
  (display-name [_] "vertical-bar-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                formatted-data (adu/format-data data
                                                (:ord-ks ks)
                                                (:num-ks ks))
                [ord-data num-data] (adu/separate-data formatted-data)
                [min-data max-data] (adu/min-max-value num-data)
                [x-scale y-scale] (construct-vertical-scale ord-data
                                                            min-data
                                                            max-data
                                                            (:padding scale)
                                                            inner-size)]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build rects
                       {:data formatted-data
                        :scale [x-scale y-scale]
                        :g {:x (:width margin)
                            :y (:height margin)}
                        :opts rects-opts}
                       {:opts {:view vertical-rect}})
             (om/build adaca/ordinal-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn x-scale
                        :opts (:opts x-axis-opts)
                        :orient (:orient x-axis-opts :bottom)
                        :data ord-data})
             (om/build adaca/numerical-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn y-scale
                        :opts  (:opts y-axis-opts)
                        :orient (:orient y-axis-opts :left)
                        :data [min-data max-data]})])))

(defn construct-horizontal-scale
  [ord-data min-data max-data padding inner-size]
  [(linear-scale {:domain [min-data max-data]
                  :range-scale [0 (:width inner-size)]})
   (ordinal-scale {:domain ord-data
                   :range-bands [[0 (:height inner-size)] padding]})])

(defcomponent horizontal-bar-chart
  [{:keys [size scale ks rects-opts x-axis-opts y-axis-opts data]} owner]
  (display-name [_] "horizontal-bar-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                formatted-data (adu/format-data data
                                                (:ord-ks ks)
                                                (:num-ks ks))
                [ord-data num-data] (adu/separate-data formatted-data)
                [min-data max-data] (adu/min-max-value num-data)
                [x-scale y-scale] (construct-horizontal-scale ord-data
                                                              min-data
                                                              max-data
                                                              (:padding scale)
                                                              inner-size)]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build rects
                       {:data formatted-data
                        :scale [x-scale y-scale]
                        :g {:x (:width margin)
                            :y (:height margin)}
                        :opts rects-opts}
                       {:opts {:view horizontal-rect}})
             (om/build adaca/numerical-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn x-scale
                        :opts  (:opts x-axis-opts)
                        :orient (:orient x-axis-opts :bottom)
                        :data [min-data max-data]})
             (om/build adaca/ordinal-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn y-scale
                        :opts (:opts y-axis-opts)
                        :orient (:orient y-axis-opts :left)
                        :data ord-data})])))
