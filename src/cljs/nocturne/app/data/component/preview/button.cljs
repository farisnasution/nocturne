(ns cljs.nocturne.app.data.component.preview.button
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.button :only [button]]
        [cljs.nocturne.util.io :only [external-get-request]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defn fetch-preview
  [_ url parent-ch]
  (go
    (let [result (<! (external-get-request url {}))]
      (put! parent-ch result))))

(defcomponent preview-button
  [{:keys [preview-url]} owner {:keys [parent-ch]}]
  (display-name [_] "preview-button")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch %)
                 :error false}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [event (<! ch)]
                    (fetch-preview event preview-url parent-ch))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:CLICK ue/event-type) callback-fn)))
  (render-state [_ {:keys [error]}]
                (om/build button
                          {:content "Preview"}
                          {:state {:classes (if error
                                              "btn-danger"
                                              "btn-primary")}})))
