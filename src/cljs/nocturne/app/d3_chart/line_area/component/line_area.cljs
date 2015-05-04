(ns cljs.nocturne.app.d3-chart.line-area.component.line-area
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent line-area
  [{:keys [g opts d]} owner]
  (display-name [_] "line-area")
  (render [_]
          [:g {:transform (adu/translate g)}
           (om/build ads/path (assoc opts :d d))]))
