(ns cljs.nocturne.app.d3-chart.donut-pie.component.slice
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent slice
  [{:keys [path text]} owner]
  (display-name [_] "slice")
  (render [_]
          [:g {}
           (om/build ads/path path)
           (when (-> text :show? true?)
             [:g {:transform (adu/translate (:g text))}
              (om/build ads/text text)])]))

(defcomponent datafied-slice
  [{:keys [path-constructor text-constructor data]} owner]
  (display-name [_] "datafied-slice")
  (render [_]
          (om/build slice (let [ord-data (-> data .-data first)]
                            {:path (path-constructor ord-data data)
                             :text (text-constructor ord-data data)}))))
