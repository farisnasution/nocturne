(ns cljs.nocturne.app.d3-chart.bar.component.rects
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent rects
  [{:keys [g data scale opts]} owner {:keys [view]}]
  (display-name [_] "rects")
  (render [_]
          [:g {:transform (adu/translate g)}
           (om/build-all view
                         (map-indexed (fn [idx d]
                                        {:data d
                                         :scale scale
                                         :opts opts
                                         :react-key idx})
                                      data)
                         {:key :react-key})]))
