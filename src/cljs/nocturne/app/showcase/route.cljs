(ns cljs.nocturne.app.showcase.route
  (:require [secretary.core :as secretary :refer-macros [defroute]])
  (:use [cljs.nocturne.state :only [app-state]]))

(defroute show-all-showcase
  "/user/:user-slug/showcase"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:showcase :read [us]]))
         user-slug))

(defroute show-single-showcase
  "/user/:user-slug/showcase/view/:showcase-slug"
  [user-slug showcase-slug]
  (swap! app-state
         (fn [current us ss]
           (assoc current :content [:showcase :read [us ss]]))
         user-slug
         showcase-slug))

(defroute create-new-showcase
  "/user/:user-slug/showcase/new"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:showcase :write [us]]))
         user-slug))
