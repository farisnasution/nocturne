(ns cljs.nocturne.app.d3-chart.shape
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent rect
  [data owner]
  (display-name [_] "rect")
  (render [_]
          [:rect (select-keys data
                              [:fill :width :height :stroke
                               :stroke-width :x :y :rx :ry :transform])]))

(defcomponent text
  [data owner]
  (display-name [_] "text")
  (render [_]
          [:text (select-keys data
                              [:x :y :dx :dy
                               :text-anchor :transform])
           (:content data)]))

(defcomponent line
  [data owner]
  (display-name [_] "line")
  (render [_]
          [:line (select-keys data
                              [:x1 :y1 :x2 :y2
                               :stroke :stroke-width :transform])]))

(defcomponent path
  [data owner]
  (display-name [_] "path")
  (render [_]
          [:path (select-keys data
                              [:d :stroke :stroke-width :fill :transform])]))

(defcomponent circle
  [data owner]
  (display-name [_] "circle")
  (render [_]
          [:circle (select-keys data [:cx :cy :r :fill :transform])]))
