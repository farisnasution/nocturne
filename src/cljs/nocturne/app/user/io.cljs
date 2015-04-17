(ns cljs.nocturne.app.user.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-user
  [{:keys [slug params headers]}]
  (let [url (str "/api/v1/user" (when slug
                                  (str "/" slug)))]
    (ui/get-request url params headers)))

(defn save-user
  [params {:keys [headers]}]
  (let [url "/api/v1/user"]
    (ui/post-request url params headers)))

(defn update-user
  [slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" slug)]
    (ui/put-request url params headers)))

(defn remove-user
  [slug {:keys [headers]}]
  (let [url (str "/api/v1/user/" slug)]
    (ui/delete-request url params headers)))
