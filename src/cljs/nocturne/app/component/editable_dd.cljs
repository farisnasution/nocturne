(ns cljs.nocturne.app.component.editable-dd
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq])
  (:use [cljs.nocturne.app.component.field.validated :only [validated-field]]
        [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.button :only [button]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]
               [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent dd
  [{:keys [content]} owner {:keys [parent-ch]}]
  (display-name [_] "dd")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch %)}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [e (<! ch)]
                    (put! parent-ch [:content [e]]))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:DBLCLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:DBLCLICK ue/event-type) callback-fn)))
  (render [_]
          [:dd {}
           content]))

(defcomponent dd-field
  [data owner {:keys [parent-ch
                      val-fns
                      id
                      title
                      view
                      view-opts]}]
  (display-name [_] "dd-field")
  (init-state [_]
              (let [field-ch (chan)
                    button-ch (chan)]
                {:field-ch field-ch
                 :button-ch button-ch
                 :callback-fn (fn [e]
                                (ue/prevent-default e)
                                (put! button-ch e))
                 :value nil
                 :error? false}))
  (will-mount [_]
              (let [field-ch (om/get-state owner :field-ch)
                    button-ch (om/get-state owner :button-ch)]
                (go-loop []
                  (let [[v ch] (alts! [field-ch button-ch])]
                    (condp = ch
                      field-ch (let [[_ error? [value event]] v]
                                 (om/update-state! owner
                                                   (fn [current]
                                                     (assoc current :value value
                                                                    :error? error?))))
                      button-ch (let [error? (om/get-state owner :error?)
                                      text-value (om/get-state owner :value)]
                                  (put! parent-ch [:field [error? v text-value]]))))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   cancel-node (uq/by-id "cancel-button")
                   submit-node (uq/by-id "submit-button")]
               (ue/listen cancel-node (:CLICK ue/event-type) callback-fn)
               (ue/listen submit-node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
              (let [callback-fn (om/get-state owner :callback-fn)
                   cancel-node (uq/by-id "cancel-button")
                   submit-node (uq/by-id "submit-button")]
               (ue/unlisten cancel-node (:CLICK ue/event-type) callback-fn)
               (ue/unlisten submit-node (:CLICK ue/event-type) callback-fn)))
  (render-state [_ {:keys [value field-ch error?]}]
                [:dd {}
                 [:form {:class "form-horizontal"}
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch field-ch
                                    :val-fns val-fns
                                    :id id
                                    :title title
                                    :view view
                                    :view-size-class "col-xs-4"
                                    :view-opts view-opts}
                             :state {:value value}})
                  [:div {:class "form-group"}
                   [:div {:style {:margin-left "23.6%"}}
                    (om/build button
                              {:content "Cancel"}
                              {:opts {:id "cancel-button"}
                               :state {:classes "btn-xs btn-warning"}})
                    (om/build button
                              {:content "Submit"}
                              {:opts {:classes "btn-xs btn-primary"
                                      :id "submit-button"}
                               :state {:disabled? error?}})]]]]))

(defn handle-editable-dd
  [data ks owner from value]
  (condp = from
    :content (om/set-state! owner :editing? true)
    :field (let [[error? event text-value] value
                 which-button (ue/event->id event)]
             (condp = which-button
               "cancel-button" (om/set-state! owner :editing? false)
               "submit-button" (do
                                 (om/set-state! owner :editing? false)
                                 (when-not error?
                                   (om/update! data ks text-value)))))))

(defcomponent editable-dd
  [data owner {:keys [ks
                      val-fns
                      id
                      title
                      view
                      view-opts]}]
  (display-name [_] "editable-dd")
  (init-state [_]
              {:ch (chan)
               :editing? false})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [[from value] (<! ch)]
                    (handle-editable-dd data ks owner from value))
                  (recur))))
  (render-state [_ {:keys [editing? ch]}]
                (if editing?
                  (om/build dd-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns val-fns
                                    :id id
                                    :title title
                                    :view view
                                    :view-opts view-opts}
                             :state {:value (get-in data ks)}})
                  (om/build dd
                            {:content (get-in data ks)}
                            {:opts {:parent-ch ch}}))))
