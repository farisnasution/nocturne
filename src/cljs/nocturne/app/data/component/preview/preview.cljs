(ns cljs.nocturne.app.data.component.preview.preview
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.nocturne.util.dev :as ud])
  (:use [cljs.nocturne.app.data.component.preview.table :only [preview-table]]
        [cljs.nocturne.app.data.component.preview.button :only [preview-button]]
        [cljs.nocturne.app.component.icon :only [icon]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn handle-preview
  [owner [response-type response]]
  (if (= response-type :ok)
    (om/update-state! owner (fn [current]
                              (assoc current :error false
                                             :data (if (map? response)
                                                     [response]
                                                     response))))
    (om/set-state! owner :error true)))

(defcomponent preview-data
  [{:keys [preview-url]} owner]
  (display-name [_] "preview-data")
  (init-state [_]
              {:ch (chan)
               :data nil
               :error false})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [result (<! ch)]
                    (handle-preview owner result))
                  (recur))))
  (render-state [_ {:keys [ch data error]}]
                [:div {:class "container-fluid"}
                 [:div {:class "row"}
                  [:div {:class "col-md-1"}
                   (om/build preview-button
                             {:preview-url preview-url}
                             {:opts {:parent-ch ch}
                              :state {:error error}
                              :react-key "preview-button"})]]
                 [:div {:class "row"}
                   [:div {:class "col-md-12"}
                    (om/build preview-table
                              data)]]]))
