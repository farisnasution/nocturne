(ns cljs.nocturne.app.chart.route
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.nocturne.app.chart.io :as aci]
            [cljs.nocturne.app.user.io :as aui]
            [cljs.core.async :as async :refer [<!]])
  (:use [cljs.nocturne.state :only [app-state]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(defroute show-all-chart
  "/user/:user-slug/chart"
  [user-slug]
  (go
    (let [[result-type result] (<! (aci/request-get-chart user-slug
                                                          {}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:chart :read [us]])
                     (assoc-in [:users :chart] rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute show-single-chart
  "/user/:user-slug/chart/view/:chart-slug"
  [user-slug chart-slug]
  (go
    (let [[result-type result] (<! (aci/request-get-chart user-slug
                                                          {:slug chart-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us ds rslt]
                 (-> current
                     (assoc :content [:chart :read [us ds]])
                     (update-in [:users :chart]
                                (fn [chart r]
                                  (into chart r))
                                rslt)))
               user-slug
               chart-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute create-new-chart
  "/user/:user-slug/chart/new"
  [user-slug]
  (go
    (let [[result-type result] (<! (aui/request-get-user {:slug user-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:chart :write [us]])
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
