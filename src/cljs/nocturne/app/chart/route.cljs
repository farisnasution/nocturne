(ns cljs.nocturne.app.chart.route
  (:require [secretary.core :as secretary :refer-macros [defroute]])
  (:use [cljs.nocturne.state :only [app-state]]))

(defroute show-all-chart
  "/user/:user-slug/chart"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:chart :read [us]]))
         user-slug))

(defroute show-single-chart
  "/user/:user-slug/chart/view/:chart-slug"
  [user-slug chart-slug]
  (swap! app-state
         (fn [current us cs]
           (assoc current :content [:chart :read [us cs]]))
         user-slug
         chart-slug))

(defroute create-new-chart
  "/user/:user-slug/chart/new"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:chart :write [us]]))
         user-slug))
