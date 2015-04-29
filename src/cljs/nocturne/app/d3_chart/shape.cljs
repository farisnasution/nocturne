(ns cljs.nocturne.app.d3-chart.shape
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent rect
  [config owner]
  (display-name [_] "rect")
  (render [_]
          [:rect (select-keys config
                              [:fill :width :height :stroke
                               :stroke-width :x :y :rx :ry :transform])]))

(defcomponent text
  [config owner]
  (display-name [_] "text")
  (render [_]
          [:text (select-keys config
                              [:x :y :dx :dy
                               :text-anchor :transform])
           (:content config)]))

(defcomponent line
  [config owner]
  (display-name [_] "line")
  (render [_]
          [:line (select-keys config
                              [:x1 :y1 :x2 :y2
                               :stroke :stroke-width :transform])]))

(defcomponent path
  [config owner]
  (display-name [_] "path")
  (render [_]
          [:path (select-keys config
                              [:d :stroke :stroke-width :fill :transform])]))

(defcomponent circle
  [config owner]
  (display-name [_] "circle")
  (render [_]
          [:circle (select-keys config [:cx :cy :r :fill :transform])]))
