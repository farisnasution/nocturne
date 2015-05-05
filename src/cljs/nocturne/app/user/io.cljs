(ns cljs.nocturne.app.user.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn request-get-user-by-token
  [token & [channel]]
  (let [url "/api/v1/user"]
    (ui/get-request url {:token token} channel)))

(defn request-get-user
  [{:keys [slug params headers]} & [channel]]
  (let [url (str "/api/v1/user" (when slug
                                  (str "/" slug)))]
    (ui/get-request url {:params params
                         :headers headers} channel)))

(defn request-post-user
  [params {:keys [headers]} & [channel]]
  (let [url "/api/v1/user"]
    (ui/post-request url {:params params
                          :headers headers} channel)))

(defn request-put-user
  [slug params {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" slug)]
    (ui/put-request url {:params params
                         :headers headers} channel)))

(defn request-delete-user
  [slug {:keys [headers]} & [channel]]
  (let [url (str "/api/v1/user/" slug)]
    (ui/delete-request url {:headers headers} channel)))
