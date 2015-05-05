(ns cljs.nocturne.app.d3-chart.line-area.component.chart
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu]
            [cljs.nocturne.app.d3-chart.scale.ordinal :as adso]
            [cljs.nocturne.app.d3-chart.axis.component.axis :as adaca])
  (:use [cljs.nocturne.app.d3-chart.line-area.scale :only [construct-scale]]
        [cljs.nocturne.app.d3-chart.line-area.component.area :only [area]]
        [cljs.nocturne.app.d3-chart.line-area.component.line :only [line]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent area-chart
  [{:keys [size area-opts x-axis-opts y-axis-opts ks data]} owner]
  (display-name [_] "area-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                {:keys [x-ks y-ks]} ks
                [y-max-ks y-min-ks] y-ks
                formatted-data (adu/format-data data x-ks y-max-ks y-min-ks)
                [x-data y-max-data y-min-data] (adu/separate-data
                                                formatted-data)
                [x-min x-max] (adu/min-max-value x-data)
                concated-y-data (concat y-max-data y-min-data)
                [y-min y-max] (adu/min-max-value concated-y-data)
                [x-scale y-scale] (construct-scale x-min
                                                   x-max
                                                   y-min
                                                   y-max
                                                   inner-size)]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build area
                       {:g {:x (:left margin)
                            :y (:top margin)}
                        :opts area-opts
                        :scale [x-scale y-scale]
                        :data formatted-data})
             (om/build adaca/numerical-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn x-scale
                        :opts  (:opts x-axis-opts)
                        :orient (:orient x-axis-opts :bottom)
                        :data [x-min x-max]})
             (om/build adaca/numerical-axis
                       {:size {:inner-size inner-size
                               :margin margin}
                        :scale-fn y-scale
                        :opts  (:opts y-axis-opts)
                        :orient (:orient y-axis-opts :left)
                        :data [y-min y-max]})])))

(defn construct-path-opts-line
  [path-opts y-ks]
  (let [color-fn (adso/category-20c)]
    (fn [idx]
      (into {:stroke (-> y-ks
                         (get idx)
                         last
                         color-fn)
             :fill "none"}
            path-opts))))

(defcomponent line-chart
  [{:keys [size line-opts x-axis-opts y-axis-opts ks data]} owner]
  (display-name [_] "line-chart")
  (render [_]
          (let [{:keys [size inner-size margin]} (adu/compute-size size)
                {:keys [x-ks y-ks]} ks
                formatted-data (apply adu/format-data data x-ks y-ks)
                [x-data & y-data] (adu/separate-data formatted-data)
                [x-min x-max] (adu/min-max-value x-data)
                concated-y-data (apply concat y-data)
                [y-min y-max] (adu/min-max-value concated-y-data)
                [x-scale y-scale] (construct-scale x-min
                                                   x-max
                                                   y-min
                                                   y-max
                                                   inner-size)
                path-constructor (construct-path-opts-line line-opts y-ks)]
            [:svg {:width (:width size)
                   :height (:height size)}
             (om/build-all line
                           (map-indexed (fn [idx d]
                                          {:g {:x (:left margin)
                                               :y (:top margin)}
                                           :opts (path-constructor idx)
                                           :scale [x-scale y-scale]
                                           :data (map vector x-data d)
                                           :react-key idx})
                                        y-data)
                           {:key :react-key})
             (om/build adaca/numerical-axis
                       {:size {:size size
                               :margin margin}
                        :scale-fn x-scale
                        :opts  (:opts x-axis-opts)
                        :orient (:orient x-axis-opts :bottom)
                        :data [x-min x-max]}
                       {:react-key "x-axis"})
             (om/build adaca/numerical-axis
                       {:size {:size size
                               :margin margin}
                        :scale-fn y-scale
                        :opts  (:opts y-axis-opts)
                        :orient (:orient y-axis-opts :left)
                        :data [y-min y-max]}
                       {:react-key "y-axis"})])))
