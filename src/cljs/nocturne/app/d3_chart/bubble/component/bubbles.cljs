(ns cljs.nocturne.app.d3-chart.bubble.component.bubbles
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu]
            [cljs.nocturne.util.dev :as ud]
            [cljs.nocturne.app.d3-chart.scale.ordinal :as adso])
  (:use [cljs.nocturne.app.d3-chart.bubble.component.bubble :only
         [datafied-bubble]]
        [cljs.nocturne.app.d3-chart.bubble.layout :only [bubble-layout]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-text
  [text-opts]
  (fn [idx d]
    (into {:dy "0.3em"
           :text-anchor "middle"
           :show? (not (zero? idx))
           :content (when-not (zero? idx) (first d))}
          text-opts)))

(defn construct-fill
  [color-fn]
  (fn [idx d]
    (if (zero? idx)
      "white"
      (color-fn (last d)))))

(defn construct-color-fn
  [data colors]
  (let [ord-data (map first data)]
    (if (nil? colors)
      (adso/category-20c)
      (adso/ordinal-scale {:domain ord-data
                           :range-scale colors}))))

(defn construct-acceptable-value
  [data]
  (js-obj "children" (apply array data)))

(defn construct-sorting
  [sorting-type]
  (condp = sorting-type
    :none false
    :ascending-name (fn [a b]
                      (compare (first a) (first b)))
    :descending-name (fn [a b]
                       (compare (first b) (first a)))
    :ascending-value (fn [a b]
                       (compare (second a) (second b)))
    :descending-value (fn [a b]
                        (compare (second b) (second a)))
    :ascending-group (fn [a b]
                       (compare (last a) (last b)))
    :descending-group (fn [a b]
                        (compare (last b) (last a)))
    false))

(defcomponent bubbles
  [{:keys [g diameter style opts data]} owner]
  (display-name [_] "datafied-bubbles")
  (render [_]
          [:g {:transform (adu/translate g)}
           (let [fill-constructor (construct-fill (construct-color-fn
                                                   data
                                                   (:colors style)))
                 text-constructor (construct-text (:text opts))
                 bubble-factory (bubble-layout {:sort-fn (construct-sorting
                                                          (:sorting-type style))
                                                :size [diameter diameter]
                                                :padding (:padding style)
                                                :value second})]
             (om/build-all datafied-bubble
                           (->> data
                                construct-acceptable-value
                                (.nodes bubble-factory)
                                (map-indexed (fn [idx d]
                                               {:fill-constructor fill-constructor
                                                :text-constructor text-constructor
                                                :data d
                                                :react-key idx})))
                           {:key :react-key}))]))
