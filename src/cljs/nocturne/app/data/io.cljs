(ns cljs.nocturne.app.data.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-data
  [user-slug {:keys [slug params headers]}]
  (let [url (str "/api/v1/user/"
                 user-slug
                 "/data"
                 (when slug
                   (str "/" slug)))]
    (ui/get-request url params headers)))

(defn save-data
  [user-slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/data")]
    (ui/post-request url params headers)))

(defn update-data
  [user-slug slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/data/" slug)]
    (ui/put-request url params headers)))

(defn remove-data
  [user-slug slug {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/data/" slug)]
    (ui/delete-request url params headers)))
