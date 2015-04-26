(ns cljs.nocturne.app.user.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-user-by-token
  [token & [channel]]
  (let [url "/api/v1/user"]
    (ui/get-request url {:token token} channel)))

(defn fetch-user
  [{:keys [slug params headers]} & [channel]]
  (let [url (str "/api/v1/user" (when slug
                                  (str "/" slug)))]
    (ui/get-request url {:params params
                         :headers headers} channel)))

(defn save-user
  [params {:keys [headers]} & [channel]]
  (let [url "/api/v1/user"]
    (ui/post-request url {:params params
                          :headers headers} channel)))

(defn update-user
  [slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" slug)]
    (ui/put-request url {:params params
                         :headers headers} channel)))

(defn remove-user
  [slug {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" slug)]
    (ui/delete-request url {:headers headers} channel)))
