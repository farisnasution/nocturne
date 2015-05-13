(ns cljs.nocturne.app.user.route
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.nocturne.app.user.io :as aui]
            [cljs.core.async :as async :refer [<!]])
  (:use [cljs.nocturne.state :only [app-state]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(defroute user-overview
  "/user/:user-slug"
  [user-slug]
  (go
    (let [[result-type result] (<! (aui/request-get-user {:slug user-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:overview :read [us]])
                     (update-in [:users] into rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute user-settings
  "/settings"
  []
  (swap! app-state
         (fn [current]
           (assoc current :content [:settings]))))

(defroute user-logout
  "/logout"
  []
  (swap! app-state
         (fn [current]
           (assoc current :content [:logout]))))
