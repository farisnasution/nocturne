(ns cljs.nocturne.app.component.info
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use [cljs.nocturne.app.user.component.avatar :only [avatar]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn get-user-from-content
  [data]
  (let [users (:users data)
        [_ _ [user-slug _]] (:content data)]
    (get users user-slug)))

(defcomponent info
  [data owner]
  (display-name [_] "info")
  (render [_]
          [:div {:class "row"}
           [:div {:class "col-sm-6"}
            (om/build avatar {:user (get-user-from-content data)})]]))
