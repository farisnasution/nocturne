(ns cljs.nocturne.app.user.component.avatar
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.component.image :only [image]]
        [cljs.nocturne.app.component.anchor :only [anchor]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent avatar
  [{:keys [image-url user-url username]} owner]
  (display-name [_] "avatar")
  (render [_]
          [:div {:class "row"}
           [:div {:class "col-sm-4 col-xs-4"}
            (om/build image {:image-url image-url})]
           [:div {:class "col-sm-8 col-xs-8"}
            (om/build anchor {:content username
                              :url user-url})]]))
