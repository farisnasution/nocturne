(ns cljs.nocturne.util.auth
  (:require [cljs.nocturne.app.user.io :as aui]
            [hodgepodge.core :as hp]))

(defn unauthenticate!
  []
  (hp/remove-item hp/local-storage "id"))

(defn authenticate!
  [token-value]
  (hp/set-item hp/local-storage "id" token-value))

(defn authenticated?
  []
  (-> hp/local-storage
      (hp/get-item "id")
      nil?
      not))

(defn self-request!
  [& [channel]]
  (let [token-id (-> hp/local-storage
                     (hp/get-item "id"))]
    (aui/request-get-user-by-token token-id channel)))
