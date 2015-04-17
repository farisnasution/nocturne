(ns cljs.nocturne.app.data.route
  (:require [secretary.core :as secretary :refer-macros [defroute]])
  (:use [cljs.nocturne.state :only [app-state]]))

(defroute show-all-data
  "/user/:user-slug/data"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:data :read [us]]))
         user-slug))

(defroute show-single-data
  "/user/:user-slug/data/view/:data-slug"
  [user-slug data-slug]
  (swap! app-state
         (fn [current us ds]
           (assoc current :content [:data :read [us ds]]))
         user-slug
         data-slug))

(defroute create-new-data
  "/user/:user-slug/data/new"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:data :write [us]]))
         user-slug))
