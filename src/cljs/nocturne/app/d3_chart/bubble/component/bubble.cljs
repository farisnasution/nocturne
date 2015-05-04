(ns cljs.nocturne.app.d3-chart.bubble.component.bubble
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent bubble
  [{:keys [g circle text]} owner]
  (display-name [_] "bubble")
  (render [_]
          [:g {:transform (adu/translate g)}
           (om/build ads/circle circle)
           (when (-> text :show? true?)
             (om/build ads/text text))]))

(defcomponent datafied-bubble
  [{:keys [react-key fill-constructor text-constructor data]} owner]
  (display-name [_] "datafied-bubble")
  (render [_]
          (om/build bubble
                    {:g {:x (.-x data)
                         :y (.-y data)}
                     :circle {:r (.-r data)
                              :fill (fill-constructor react-key data)}
                     :text (text-constructor react-key data)})))
