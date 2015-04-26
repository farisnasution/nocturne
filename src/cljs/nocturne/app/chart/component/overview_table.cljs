(ns cljs.nocturne.app.chart.component.overview-table
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.component.anchor :only [anchor]]
        [cljs.nocturne.app.data.route :only [show-single-data]]
        [cljs.nocturne.app.chart.route :only [show-single-chart]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent td
  [{:keys [content]} owner]
  (display-name [_] " td")
  (render [_]
          [:td {}
           content]))

(defcomponent anchored-td
  [{:keys [content url]} owner]
  (display-name [_] "anchored-td")
  (render [_]
          [:td {}
           (om/build anchor {:content content
                             :url url})]))

(defcomponent tr
  [{:keys [data]} owner]
  (display-name [_] "tr")
  (render [_]
          [:tr {}
           (om/build anchored-td
                     {:content (:name data)
                      :url (show-single-chart {:user-slug (:creator-slug data)
                                               :chart-slug (:slug data)})}
                     {:react-key "chart-td-name"})
           (om/build anchored-td
                     {:content (-> data :data :name)
                      :url (show-single-data {:user-slug (:creator-slug data)
                                              :data-slug (-> data
                                                             :data
                                                             :slug)})}
                     {:react-key "chart-td-data"})
           (om/build td
                     {:content (:date data)}
                     {:react-key "chart-td-date"})]))

(defcomponent tbody
  [data owner]
  (display-name [_] "tbody")
  (render [_]
          [:tbody {}
           (om/build-all tr
                         (map-indexed (fn [idx d]
                                        {:data d
                                         :react-key idx})
                                      data)
                         {:key :react-key})]))

(defcomponent th
  [{:keys [content]} owner]
  (display-name [_] "th")
  (render [_]
          [:th {}
           content]))

(defcomponent thead
  [data owner]
  (display-name [_] "thead")
  (render [_]
          [:thead {}
           (om/build th
                     {:content "Name"}
                     {:react-key "chart-th-name"})
           (om/build th
                     {:content "Data"}
                     {:react-key "chart-th-data"})
           (om/build th
                     {:content "Date"}
                     {:react-key "chart-th-date"})]))

(defcomponent chart-overview-table
  [data owner]
  (display-name [_] "overview-table")
  (render [_]
          [:div {:class "table-responsive"}
           [:table {:class "table"}
            (om/build thead
                      {}
                      {:react-key "chart-thead"})
            (om/build tbody
                      data
                      {:react-key "chart-tbody"})]]))
