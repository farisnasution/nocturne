(ns cljs.nocturne.app.component.field.validated
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.util.generic :only [not-nil?]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn rules-fn
  [rules]
  (fn [value]
    (->> rules
         (map (fn [[rule message]]
                (when-not (rule value) message)))
         (filter not-nil?))))

(defn error-class
  [error? first?]
  (cond
   (and error? (not first?)) " has-error"
   (and (not error?) (not first?)) " has-success"
   :else ""))

(defn update-error-status
  [owner messages value event]
  (om/update-state! owner (fn [current]
                            (let [first? (:first? current)]
                              (assoc current :error? (seq messages)
                                             :first? (if (true? first?)
                                                       false
                                                       first?)
                                             :messages messages
                                             :value value
                                             :event event)))))

(defcomponent validated-field
  [data owner {:keys [parent-ch
                      val-fns
                      id
                      title
                      view
                      view-size-class
                      view-opts]}]
  (display-name [_] "validated-field")
  (init-state [_]
              {:ch (chan)
               :validation-fn (rules-fn val-fns)
               :error {:state true
                       :messages nil}
               :first? true
               :value nil
               :event nil})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    validation-fn (om/get-state owner :validation-fn)]
                (go-loop []
                  (let [event (<! ch)
                        value (ue/event->value event)
                        messages (validation-fn value)]
                    (update-error-status owner messages value event))
                  (recur))))
  (did-update [_ prev-props prev-state]
              (let [current-error (om/get-state owner [:error :state])
                    current-value (om/get-state owner :value)
                    current-event (om/get-state owner :event)]
                (put! parent-ch [id
                                 current-error
                                 [current-value current-event]])))
  (render-state [_ {:keys [ch error first? value]}]
                [:div {:class (str "form-group"
                                   (error-class (:state error) first?))}
                 (when title
                   [:label {:for (str id)}
                    title])
                 [:div (if (string? view-size-class)
                         {:class view-size-class}
                         {})
                  (om/build view
                            {}
                            {:opts (merge {:parent-ch ch
                                           :id (str id)}
                                          view-opts)
                             :state {:value value}})]
                 (when (= (error-class (:state error) first?) " has-error")
                   [:div {:class "help-block with-errors"}
                    [:ul {:class "list-unstyled"}
                     (mapv (fn [message]
                             [:li {}
                              message])
                           (:messages error))]])]))
