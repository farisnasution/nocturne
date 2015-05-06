(ns cljs.nocturne.app.user.component.avatar
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.component.image :only [image]]
        [cljs.nocturne.app.component.anchor :only [anchor]]
        [cljs.nocturne.app.user.route :only [user-overview]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent avatar
  [{:keys [user]} owner]
  (display-name [_] "avatar")
  (render [_]
          [:div {:class "row"}
           [:div {:class "col-sm-4 col-xs-4"}
            (om/build image {:image-url (:avatar user)})]
           [:div {:class "col-sm-8 col-xs-8"}
            (om/build anchor {:content (:name user)
                              :url (user-overview {:user-slug
                                                   (:slug user)})})]]))
