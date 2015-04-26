(ns cljs.nocturne.app.showcase.component.overview-table
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.component.anchor :only [anchor]]
        [cljs.nocturne.app.showcase.route :only [show-single-showcase]]
        [cljs.nocturne.app.chart.route :only [show-single-chart]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent td
  [{:keys [content]} owner]
  (display-name [_] "td")
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

(defcomponent anchored-tds
  [{:keys [data]} owner]
  (display-name [_] "anchored-tds")
  (render [_]
          [:td {}
           (map (fn [d]
                  [:p {}
                   (om/build anchor d)])
                data)]))

(defcomponent tr
  [{:keys [data]} owner]
  (display-name [_] "tr")
  (render [_]
          [:tr {}
           (om/build anchored-td
                     {:content (:name data)
                      :url (show-single-showcase
                            {:user-slug (:creator-slug data)
                             :showcase-slug (:slug data)})}
                     {:react-key "showcase-td-name"})
           (om/build anchored-tds
                     {:data (map (fn [c]
                                   {:content (:name c)
                                    :url (show-single-chart
                                          {:user-slug (:creator-slug data)
                                           :chart-slug (:slug c)})})
                                 (:charts data))}
                     {:react-key "showcase-td-charts"})
           (om/build td
                     {:content (:date data)}
                     {:react-key "showcase-td-date"})]))

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
                     {:react-key "showcase-th-name"})
           (om/build th
                     {:content "Charts"}
                     {:react-key "showcase-th-charts"})
           (om/build th
                     {:content "Date"}
                     {:react-key "showcase-th-date"})]))

(defcomponent showcase-overview-table
  [data owner]
  (display-name [_] "overview-table")
  (render [_]
          [:div {:class "table-responsive"}
           [:table {:class "table"}
            (om/build thead
                      {}
                      {:react-key "showcase-thead"})
            (om/build tbody
                      data
                      {:react-key "showcase-tbody"})]]))
