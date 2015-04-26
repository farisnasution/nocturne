(ns cljs.nocturne.app.data.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-data
  [user-slug {:keys [slug params headers]} & [channel]]
  (let [url (str "/api/v1/user/"
                 user-slug
                 "/data"
                 (when slug
                   (str "/" slug)))]
    (ui/get-request url {:params params
                         :headers headers} channel)))

(defn save-data
  [user-slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/data")]
    (ui/post-request url {:params params
                          :headers headers} channel)))

(defn update-data
  [user-slug slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/data/" slug)]
    (ui/put-request url {:params params
                         :headers headers} channel)))

(defn remove-data
  [user-slug slug {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" user-slug "/data/" slug)]
    (ui/delete-request url {:headers headers} channel)))
