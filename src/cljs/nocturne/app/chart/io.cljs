(ns cljs.nocturne.app.chart.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-chart
  [user-slug {:keys [slug params headers]}]
  (let [url (str "/api/v1/user/"
                 user-slug
                 "/chart"
                 (when slug
                   (str "/" slug)))]
    (ui/get-request url params headers)))

(defn save-chart
  [user-slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/chart")]
    (ui/post-request url params headers)))

(defn update-chart
  [user-slug slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/chart/" slug)]
    (ui/put-request url params headers)))

(defn remove-chart
  [user-slug slug {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/chart/" slug)]
    (ui/delete-request url params headers)))
