(ns cljs.nocturne.app.showcase.io
  (:require [cljs.nocturne.util.io :as ui]))

(defn fetch-showcase
  [user-slug {:keys [slug params headers]}]
  (let [url (str "/api/v1/user/"
                 user-slug
                 "/showcase"
                 (when slug
                   (str "/" slug)))]
    (ui/get-request url params headers)))

(defn save-showcase
  [user-slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/showcase")]
    (ui/post-request url params headers)))

(defn update-showcase
  [user-slug slug params {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/showcase/" slug)]
    (ui/put-request url params headers)))

(defn remove-showcase
  [user-slug slug {:keys [headers]}]
  (let [url (str "/api/v1/user/" user-slug "/showcase/" slug)]
    (ui/delete-request url params headers)))
