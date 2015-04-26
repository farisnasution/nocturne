(ns cljs.nocturne.app.chart.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-chart
  [user-slug {:keys [slug params headers]} & [channel]]
  (let [url (str "/api/v1/user/"
                 user-slug
                 "/chart"
                 (when slug
                   (str "/" slug)))]
    (ui/get-request url {:params params
                         :headers headers} channel)))

(defn save-chart
  [user-slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/chart")]
    (ui/post-request url {:params params
                          :headers headers} channel)))

(defn update-chart
  [user-slug slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/chart/" slug)]
    (ui/put-request url {:params params
                         :headers headers} channel)))

(defn remove-chart
  [user-slug slug {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/chart/" slug)]
    (ui/delete-request url {:headers headers} channel)))
