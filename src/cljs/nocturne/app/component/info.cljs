(ns cljs.nocturne.app.component.info
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.user.component.avatar :only [avatar]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent info
  [{:keys [image-url user-url username]} owner]
  (display-name [_] "info")
  (render [_]
          [:div {:class "row"}
           [:div {:class "col-sm-6"}
            (om/build avatar {:image-url image-url
                              :user-url user-url
                              :username username})]]))
