(ns cljs.nocturne.app.data.component.preview-table
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn extract-keys
  [coll]
  (reduce (fn [p n]
            (into p (keys n))) #{} coll))

(defcomponent td
  [{:keys [content]} owner]
  (display-name [_] "td")
  (render [_]
          [:td {}
           content]))

(defcomponent tr
  [{:keys [data ks]} owner]
  (display-name [_] "tr")
  (render [_]
          [:tr {}
           (om/build-all td
                         (map-indexed (fn [idx k]
                                        {:content (get data k)
                                         :react-key idx}) ks)
                         {:key :react-key})]))

(defcomponent tbody
  [{:keys [data ks]} owner]
  (display-name [_] "tbody")
  (render [_]
          [:tbody {}
           (om/build-all tr
                         (map-indexed (fn [idx d]
                                        {:data d
                                         :ks ks
                                         :react-key idx}) data)
                         {:key :react-key})]))

(defcomponent th
  [{:keys [content]} owner]
  (display-name [_] "th")
  (render [_]
          [:th {}
           content]))

(defcomponent thead
  [{:keys [contents]} owner]
  (display-name [_] "thead")
  (render [_]
          [:thead {}
           (om/build-all th
                         (map-indexed (fn [idx c]
                                        {:content c
                                         :react-key idx}) contents)
                         {:key :react-key})]))

(defcomponent display-table
  [data owner]
  (display-name [_] "display-table")
  (render [_]
          [:div {:class "table-responsive"}
           (let [ks (extract-keys data)]
             [:table {:class "table"}
              (om/build thead
                        {:contents ks}
                        {:react-key "display-thead"})
              (om/build tbody
                        {:data data
                         :ks ks}
                        {:react-key "display-tbody"})])]))
