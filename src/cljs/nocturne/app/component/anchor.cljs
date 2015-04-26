(ns cljs.nocturne.app.component.anchor
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.app.component.icon :as aci])
  (:use-macros [cljs.core.async.macros :only [go-loop]]
               [cljs.nocturne.macro :only [defcomponent]]))

(defn redirect-callback
  [e]
  (-> e ue/event->path ur/dispatch!))

(defcomponent anchor
  [{:keys [content url]} owner]
  (display-name [_] "anchor")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn (fn [e]
                                (ue/prevent-default e)
                                (put! ch e))}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [e (<! ch)]
                    (redirect-callback e))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:CLICK ue/event-type) callback-fn)))
  (render [_]
          [:a {:href url}
           content]))

(defcomponent iconed-anchor
  [{:keys [content url icon-name]} owner {:keys [extra]}]
  (display-name [_] "iconed-anchor")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn (fn [e]
                                (ue/prevent-default e)
                                (put! ch e))}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [e (<! ch)]
                    (redirect-callback e))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:CLICK ue/event-type) callback-fn)))
  (render [_]
          [:a {:href url}
           (om/build aci/icon
                     {:icon-name icon-name}
                     {:opts {:extra extra}})
           " "
           content]))
