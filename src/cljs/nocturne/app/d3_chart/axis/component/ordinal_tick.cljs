(ns cljs.nocturne.app.d3-chart.axis.component.ordinal-tick
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.d3-chart.axis.component.tick :only [tick]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-g
  [scale-fn orient data]
  (let [value (+ (scale data)
                 (/ (.rangeBand scale) 2))]
    (cond
     (or (= orient :top)
         (= orient :bottom)) {:x value}
     (or (= orient :left)
         (= orient :right)) {:y value})))

(defcomponent ordinal-tick
  [{:keys [scale-fn orient opts data]} owner]
  (display-name [_] "ordinal-tick")
  (render [_]
          (om/build tick
                    {:g (construct-g scale-fn orient data)
                     :orient orient
                     :opts opts
                     :data data})))

(defcomponent ordinal-ticks
  [{:keys [scale-fn orient opts data]} owner]
  (display-name [_] "ordinal-ticks")
  (render [_]
          [:g {}
           (om/build-all ordinal-tick
                         (map-indexed (fn [idx d]
                                        {:scale-fn scale-fn
                                         :orient orient
                                         :opts opts
                                         :data d
                                         :react-key idx})
                                      data)
                         {:key :react-key})]))
