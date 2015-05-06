(ns cljs.nocturne.app.data.component.show-data
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.textarea :only [textarea-field]]
        [cljs.nocturne.app.component.editable-dd :only [editable-dd]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defcomponent dt
  [{:keys [content]} owner]
  (display-name [_] "dt")
  (render [_]
          [:dt {}
           content]))

(defcomponent dd
  [{:keys [content]} owner]
  (display-name [_] "dd")
  (render [_]
          [:dd {}
           content]))

(defcomponent data-dl
  [data owner]
  (display-name [_] "dl")
  (render [_]
          [:dl {:class "dl-horizontal"}
           (om/build dt
                     {:content "Name"}
                     {:react-key "name-dt"})
           (om/build dd
                     {:content (:name data)}
                     {:react-key "name-dd"})
           (om/build dt
                     {:content "Url"}
                     {:react-key "url-dt"})
           (om/build dd
                     {:content (:url data)}
                     {:react-key "url-dd"})
           (om/build dt
                     {:content "Description"}
                     {:react-key "description-dt"})
           (om/build dd
                     {:content (:description data)}
                     {:react-key "description-dd"})]))

;; (defcomponent data-dl
;;   [data owner]
;;   (display-name [_] "dl")
;;   (render [_]
;;           [:dl {:class "dl-horizontal"}
;;            (om/build dt {:content "Name"})
;;            (om/build editable-dd
;;                      data
;;                      {:opts {:ks [:name]
;;                              :val-fns [[#(not (empty? %))
;;                                         "fjewhfewjfhewfhjj"]]
;;                              :id :name
;;                              :view input-field
;;                              :view-opts {:field-type "text"}}})
;;            (om/build dt {:content "Url"})
;;            (om/build editable-dd
;;                      data
;;                      {:opts {:ks [:url]
;;                              :val-fns []
;;                              :id :url
;;                              :view input-field
;;                              :view-opts {:field-type "text"}}})
;;            (om/build dt {:content "Description"})
;;            (om/build editable-dd
;;                      data
;;                      {:opts {:ks [:description]
;;                              :val-fns []
;;                              :id :description
;;                              :view textarea-field}})]))
