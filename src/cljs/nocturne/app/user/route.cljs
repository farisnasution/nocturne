(ns cljs.nocturne.app.user.route
  (:require [secretary.core :as secretary :refer-macros [defroute]])
  (:use [cljs.nocturne.state :only [app-state]]))

(defroute user-overview
  "/user/:user-slug"
  [user-slug]
  (swap! app-state
         (fn [current us]
           (assoc current :content [:overview :read [us]]))
         user-slug))
