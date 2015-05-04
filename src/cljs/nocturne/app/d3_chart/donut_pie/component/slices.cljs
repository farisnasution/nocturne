(ns cljs.nocturne.app.d3-chart.donut-pie.component.slices
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.scale.ordinal :as adso]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use [cljs.nocturne.app.d3-chart.donut-pie.component.slice :only
         [datafied-slice]]
        [cljs.nocturne.app.d3-chart.donut-pie.layout :only
         [donut-pie-layout]]
        [cljs.nocturne.app.d3-chart.donut-pie.construct :only
         [donut-pie-constructor]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-path
  [path-opts donut-pie-fn color-fn]
  (fn [ord-data num-data]
    (into {:fill (color-fn ord-data)
           :d (donut-pie-fn num-data)}
          path-opts)))

(defn construct-text
  [text-opts donut-pie-fn]
  (fn [ord-data num-data]
    (let [centroid (.centroid donut-pie-fn num-data)
          g {:x (first centroid)
             :y (second centroid)}]
      (into {:g g
             :content ord-data
             :show-text? true
             :text-anchor "middle"}
            text-opts))))

(defcomponent slices
  [{:keys [g layout-fn constructor-fn color-fn opts data]} owner]
  (display-name [_] "slices")
  (render [_]
          (let [path-constructor (construct-path (:path opts)
                                                 constructor-fn
                                                 color-fn)
                text-constructor (construct-text (:text opts)
                                                 constructor-fn)]
            [:g {:transform (adu/translate g)}
             (om/build-all datafied-slice
                           (->> (apply array data)
                                layout-fn
                                (map-indexed (fn [idx d]
                                               {:path-constructor path-constructor
                                                :text-constructor text-constructor
                                                :data d
                                                :react-key idx})))
                           {:key :react-key})])))

(defn construct-color-fn
  [data colors]
  (let [ord-data (map first data)]
    (if (nil? colors)
      (adso/category-20c)
      (adso/ordinal-scale {:domain ord-data
                           :range-scale colors}))))

(defcomponent datafied-slices
  [{:keys [g colors scale opts data]} owner]
  (display-name [_] "datafied-slices")
  (render [_]
          (om/build slices
                    {:g g
                     :layout-fn (donut-pie-layout {:value (fn [d] (second d))
                                                   :sort-fn nil})
                     :constructor-fn (donut-pie-constructor
                                      {:outer-radius (:outer scale)
                                       :inner-radius (:inner scale)})
                     :color-fn (construct-color-fn data colors)
                     :data data
                     :opts opts})))
