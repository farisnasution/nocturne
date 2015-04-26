(ns cljs.nocturne.app.data.component.show-data
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.textarea :only [textarea-field]]
        [cljs.nocturne.app.component.field.validated :only [validated-field]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defcomponent dt
  [{:keys [content]} owner]
  (display-name [_] "dt")
  (render [_]
          [:dt {}
           content]))
r
(defcomponent dd
  [{:keys [content]} owner]
  (display-name [_] "dd")
  (render [_]
          [:dd {}
           content]))

(defn handle-attempt-to-edit
  [data id owner error value event]
  (condp
      (and (nil? error)
           (= (:ENTER ue/keycodes)
              (ue/keycode event))) (do
                                     (om/update! data id value)
                                     (om/set-state! owner :editing? false))
    (= (:ESC ue/keycodes) (ue/keycode event)) (om/set-state! owner
                                                             :editing?
                                                             false)))

(defcomponent editable-dd
  "TODO: val-fns"
  [data owner {:keys [id
                      error-title]}]
  (display-name [_] "editable-dd")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch [:self nil %])
                 :editing? false}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [[from meta-info value] (<! ch)]
                    (condp = from
                      :self (om/set-state! owner :editing? true)
                      :name (let [error meta-info
                                  [real-value event] value]
                              (handle-attempt-to-edit data
                                                      id
                                                      owner
                                                      error
                                                      real-value
                                                      event))))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:DBLCLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:DBLCLICK ue/event-type) callback-fn)))
  (render-state [_ {:keys [editing? ch]}]
                (if editing?
                  (om/build validated-field
                            {:content (id data)}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               (str error-title " cannot empty.")]]
                                    :id id
                                    :view  input-field
                                    :view-opts {:field-type "text"}}})
                  (om/build dd {:content (id data)}))))

(defcomponent data-dl
  [data owner]
  (display-name [_] "dl")
  (render [_]
          [:dl {}
           (om/build dt {:content "Name"})
           (om/build editable-dd data {:opts {:id :name}})
           (om/build dt {:content "Url"})
           (om/build editable-dd data {:opts {:id :url}})
           (om/build dt {:content "Description"})
           (om/build dd {:content description})
           (om/build editable-dd data {:opts {:id :description}})]))
