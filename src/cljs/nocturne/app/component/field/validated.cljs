(ns cljs.nocturne.app.component.field.validated
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn rules-fn
  [rules]
  (fn [value]
    (->> rules
         (map (fn [[rule message]]
                (when-not (rule value) message)))
         (filter #(not (nil? %)))
         first)))

(defn error-class
  [error? first?]
  (str "form-group"
       (cond
        (and error? (not first?)) " has-error"
        (and (not error?) (not first?)) " has-success")))

(defn update-error-status
  [owner message value event]
  (om/update-state! owner (fn [current]
                            (let [first? (:first? current)]
                              (assoc current :error? (not (nil? message))
                                             :first? (if (true? first?)
                                                       false
                                                       first?)
                                             :message message
                                             :value value
                                             :event event)))))

(defcomponent validated-field
  [{:keys [value]} owner {:keys [parent-ch
                                 val-fns
                                 id
                                 title
                                 view
                                 view-opts]}]
  (display-name [_] "validated-field")
  (init-state [_]
              {:ch (chan)
               :validation-fn (rules-fn val-fns)
               :error? true
               :first? true
               :message nil
               :value nil
               :event nil})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    validation-fn (om/get-state owner :validation-fn)]
                (go-loop []
                  (let [event (<! ch)
                        message (validation-fn (ue/event->value event))]
                    (update-error-status owner message value event))
                  (recur))))
  (did-update [_ prev-props prev-state]
              (let [;prev-error (:error? prev-state)
                    current-error (om/get-state owner :error?)
                    current-value (om/get-state owner :value)
                    current-event (om/get-state owner :event)]
                (put! parent-ch [id
                                 current-error
                                 [current-value current-event]])
                ;; (when-not (= prev-error current-error)
                ;;   (put! parent-ch [id
                ;;                    current-error
                ;;                    [current-value current-event]]))
                ))
  (render-state [_ {:keys [ch error? first? message]}]
                [:div {:class (str "form-group" (error-class error? first?))}
                 (when title
                   [:label {:for (str id)}
                    title])
                 (om/build view
                           {:value value}
                           {:opts (merge {:parent-ch ch
                                          :id (str id)}
                                         view-opts)})
                 (when (= (error-class error? first?) " has-error")
                   [:div {:class "help-block with-errors"}
                    [:ul {:class "list-unstyled"}
                     [:li {}
                      message]]])]))
