(ns cljs.nocturne.app.component.field.input
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.util.generic :only [not-nil?]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defcomponent input-field
  [data owner {:keys [parent-ch
                      id
                      placeholder
                      field-type]}]
  (display-name [_] "input-field")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch %)
                 :value nil
                 :event nil}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [event (<! ch)
                        value (ue/event->value event)]
                    (om/update-state! owner (fn [current]
                                              (assoc current :value value
                                                             :event event))))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:BLUR ue/event-type) callback-fn)
               (ue/listen node (:KEYUP ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:BLUR ue/event-type) callback-fn)
                  (ue/unlisten node (:KEYUP ue/event-type) callback-fn)))
  (did-update [_ prev-props prev-state]
              (let [current-event (om/get-state owner :event)]
                (when (and (not-nil? current-event)
                           (not-nil? parent-ch) )
                  (put! parent-ch current-event))))
  (render-state [_ {:keys [value]}]
                [:input {:style {:border-radius 0}
                         :id id
                         :type field-type
                         :placeholder placeholder
                         :class "form-control"
                         :value value
                         :onChange (fn [e]
                                     )}]))
