(ns cljs.nocturne.app.data.route
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.nocturne.app.data.io :as adi]
            [cljs.nocturne.app.user.io :as aui]
            [cljs.core.async :as async :refer [<!]])
  (:use [cljs.nocturne.state :only [app-state]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(defroute show-all-data
  "/user/:user-slug/data"
  [user-slug]
  (go
    (let [[result-type result] (<! (adi/request-get-data user-slug
                                                         {}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:data :read [us]])
                     (assoc-in [:users :data] rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute show-single-data
  "/user/:user-slug/data/view/:data-slug"
  [user-slug data-slug]
  (go
    (let [[result-type result] (<! (adi/request-get-data user-slug
                                                         {:slug data-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us ds rslt]
                 (-> current
                     (assoc :content [:data :read [us ds]])
                     (update-in [:users :data]
                                (fn [data r]
                                  (into data r))
                                rslt)))
               user-slug
               data-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute create-new-data
  "/user/:user-slug/data/new"
  [user-slug]
  (go
    (let [[result-type result] (<! (aui/request-get-user {:slug user-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:data :write [us]])
                     (update-in [:users]
                                (fn [users r]
                                  (into users r))
                                rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))
