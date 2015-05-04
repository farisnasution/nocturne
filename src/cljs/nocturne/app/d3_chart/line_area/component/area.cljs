(ns cljs.nocturne.app.d3-chart.line-area.component.area
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu]
            [cljs.nocturne.util.dev :as ud]
            [cljs.nocturne.app.d3-chart.line-area.construct :as adlc])
  (:use [cljs.nocturne.app.d3-chart.line-area.component.line-area :only
         [line-area]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-path-opts
  [path-opts]
  (into {:fill "steelblue"}
        path-opts))

(defcomponent area
  [{:keys [g opts scale data]} owner]
  (display-name [_] "area")
  (render [_]
          (let [[x-scale y-scale] scale
                constructor (adlc/area-constructor {:x #(x-scale (first %))
                                                    :y0 (fn [d]
                                                          (if (= 3 (count d))
                                                            (y-scale (last d))
                                                            (y-scale 0)))
                                                    :y1 #(y-scale (second %))})]
            (om/build line-area
                      {:g g
                       :opts (construct-path-opts opts)
                       :d (->> data
                               (sort (fn [a b] (compare (first a) (first b))))
                               (apply array)
                               constructor)}))))
