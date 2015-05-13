(ns cljs.nocturne.app.showcase.route
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.nocturne.app.showcase.io :as asi]
            [cljs.nocturne.app.user.io :as aui]
            [cljs.core.async :as async :refer [<!]])
  (:use [cljs.nocturne.state :only [app-state]])
  (:use-macros [cljs.core.async.macros :only [go]]))

(defroute show-all-showcase
  "/user/:user-slug/showcase"
  [user-slug]
  (go
    (let [[result-type result] (<! (asi/request-get-showcase user-slug
                                                             {}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:showcase :read [us]])
                     (assoc-in [:users :showcase] rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute show-single-showcase
  "/user/:user-slug/showcase/view/:showcase-slug"
  [user-slug showcase-slug]
  (go
    (let [[result-type result] (<! (asi/request-get-showcase
                                    user-slug
                                    {:slug showcase-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us ds rslt]
                 (-> current
                     (assoc :content [:showcase :read [us ds]])
                     (update-in [:users :showcase] into rslt)))
               user-slug
               showcase-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))

(defroute create-new-showcase
  "/user/:user-slug/showcase/new"
  [user-slug]
  (go
    (let [[result-type result] (<! (aui/request-get-user {:slug user-slug}))]
      (if (= result-type :ok)
        (swap! app-state
               (fn [current us rslt]
                 (-> current
                     (assoc :content [:showcase :write [us]])
                     (update-in [:users] into rslt)))
               user-slug
               result)
        (swap! app-state
               (fn [current rslt]
                 (assoc current :content [:error rslt]))
               result)))))
