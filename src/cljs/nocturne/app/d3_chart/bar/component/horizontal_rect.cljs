(ns cljs.nocturne.app.d3-chart.bar.component.horizontal-rect
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.math :as um])
  (:use [cljs.nocturne.app.d3-chart.shape :only [rect]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn compute-width
  [x-scale data]
  (um/->pos (- (x-scale data)
               (x-scale 0))))

(defn compute-height
  [y-scale]
  (.rangeBand y-scale))

(defn compute-x-pos
  [x-scale data]
  (if (pos? data)
    (x-scale 0)
    (x-scale data)))

(defn compute-y-pos
  [y-scale data]
  (y-scale data))

(defcomponent horizontal-rect
  [{[ord-d num-d] :data [x-scale y-scale] :scale} owner opts]
  (display-name [_] "vertical-rect")
  (render [_]
          (om/build rect (merge {:width (compute-width x-scale num-d)
                                 :height (compute-height y-scale)
                                 :x (compute-x-pos x-scale num-d)
                                 :y (compute-y-pos y-scale ord-d)}
                                opts))))
