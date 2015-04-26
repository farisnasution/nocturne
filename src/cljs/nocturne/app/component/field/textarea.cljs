(ns cljs.nocturne.app.component.field.textarea
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defcomponent textarea-field
  [data owner {:keys [parent-ch
                      placeholder
                      field-id]}]
  (display-name [_] "textarea-field")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch %)}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [event (<! ch)]
                    (put! parent-ch (ue/event->value event)))
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
  (render [_]
          [:textarea {:style {:border-radius 0}
                      :id field-id
                      :placeholder placeholder
                      :class "form-control"}]))
