(ns cljs.nocturne.app.d3-chart.bar.component.vertical-rect
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.math :as um])
  (:use [cljs.nocturne.app.d3-chart.shape :only [rect]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn compute-width
  [x-scale]
  (.rangeBand x-scale))

(defn compute-height
  [y-scale data]
  (um/->pos (- (y-scale data)
               (y-scale 0))))

(defn compute-x-pos
  [x-scale data]
  (x-scale data))

(defn compute-y-pos
  [y-scale data]
  (if (neg? data)
    (y-scale 0)
    (y-scale data)))

(defcomponent vertical-rect
  [{[ord-d num-d] :data
    [x-scale y-scale] :scale
    opts :opts} owner]
  (display-name [_] "vertical-rect")
  (render [_]
          (om/build rect (merge {:width (compute-width x-scale)
                                 :height (compute-height y-scale num-d)
                                 :x (compute-x-pos x-scale ord-d)
                                 :y (compute-y-pos y-scale num-d)}
                                opts))))
