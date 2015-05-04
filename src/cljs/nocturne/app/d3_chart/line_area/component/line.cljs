(ns cljs.nocturne.app.d3-chart.line-area.component.line
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu]
            [cljs.nocturne.util.dev :as ud]
            [cljs.nocturne.app.d3-chart.line-area.construct :as adlc])
  (:use [cljs.nocturne.app.d3-chart.line-area.component.line-area :only
         [line-area]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent line
  [{:keys [g opts scale data]} owner]
  (display-name [_] "line")
  (render [_]
          (let [[x-scale y-scale] scale
                constructor (adlc/line-constructor {:x #(x-scale (first %))
                                                    :y #(y-scale (second %))})]
            (om/build line-area
                      {:g g
                       :opts opts
                       :d (->> data
                               (sort (fn [a b] (compare (first a) (first b))))
                               (apply array)
                               constructor)}))))
