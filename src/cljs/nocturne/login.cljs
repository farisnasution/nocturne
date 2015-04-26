(ns cljs.nocturne.login
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.component.login-form :only [login-form]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent login-page
  [data owner]
  (display-name [_] "login-page")
  (render [_]
          [:div {}
           (om/build login-form {} {:opts {:login-url "/okay"}})]))
