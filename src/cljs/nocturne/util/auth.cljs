(ns cljs.nocturne.util.auth
  (:require [cljs.nocturne.util.state :as us]
            [cljs.nocturne.util.cookies :as uc]
            [cljs.nocturne.app.user.io :as aui]))

(defn unauthenticate!
  []
  (-> (us/get-cookies)
      (uc/remove-cookies-value! "id")))

(defn authenticate!
  [token-value]
  (-> (us/get-cookies)
      (uc/set-cookies-value! "id" token-value)))

(defn authenticated?
  []
  (-> (us/get-cookies)
      (uc/get-cookies-value "id")
      nil?
      not))

(defn fetch-self!
  [& [channel]]
  (let [token-id (-> (us/get-cookies)
                     (uc/get-cookies-value "id"))]
    (aui/fetch-user-by-token token-id channel)))
