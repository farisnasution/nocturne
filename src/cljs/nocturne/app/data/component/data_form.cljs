(ns cljs.nocturne.app.data.component.data-form
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq]
            [cljs.nocturne.util.state :as us]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.app.data.io :as adi]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.textarea :only [textarea-field]]
        [cljs.nocturne.app.component.field.validated :only [validated-field]]
        [cljs.nocturne.app.component.button :only [button]]
        [cljs.nocturne.app.data.route :only [show-single-data]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defn handle-form-submission
  [owner ch _]
  (let [data-name-error (om/get-state owner [:data-name :error?])
        url-error (om/get-state owner [:url :error?])
        description-error (om/get-state owner [:description :error?])]
    (when (and (false? data-name-error)
               (false? url-error)
               (false? description-error))
      (let [data-name (om/get-state owner [:data-name :value])
            url (om/get-state owner [:url :value])
            description (om/get-state owner [:description :value])]
        (put! ch [:form :atom [data-name url description]])))))

(defn make-post-request
  [ch user-slug [data-name data-url description]]
  (go
    (let [v (<! (adi/request-post-data user-slug
                                       {:name data-name
                                        :url data-url
                                        :description description}
                                       {}))]
      (put! ch [:form :response v]))))

(defn make-put-request
  [ch [user-slug data-slug] data-content [data-name data-url description]]
  (go
    (let [new-value (into data-content
                          {:name data-name
                           :url data-url
                           :description description})
          v (<! (adi/request-put-data user-slug
                                      data-slug
                                      new-value
                                      {}))]
      (put! ch [:form :response [v new-value]]))))

(defn handle-post-response
  [data owner [response-type response]]
  (if (= response-type :ok)
    (let [data-slug (:slug response)
          next-path (show-single-data {:user-slug (:creator-slug response)
                                       :data-slug data-slug})
          _ (om/transact! data (fn [current]
                                 (assoc current data-slug response)))]
      (ur/dispatch! (us/get-history) next-path))
    (om/set-state! owner :form-message response)))

(defn handle-put-response
  [data owner [response-type response new-value]]
  (if (= response-type :ok)
    (let [data-slug (:slug new-value)
          next-path (show-single-data {:user-slug (:creator-slug new-value)
                                       :data-slug data-slug})
          _ (om/transact! data (fn [current]
                                 (assoc current data-slug new-value)))]
      (ur/dispatch! (us/get-history) next-path))
    (om/set-state! owner :form-message response)))

(defn update-field-state
  [ks]
  (fn [owner error [value _]]
    (om/update-state! owner
                      ks
                      (fn [current]
                        (assoc current :value value
                                       :error? error)))))

(defn get-data
  [data content]
  (let [[_ _ [_ data-slug]] content]
    (get data data-slug)))

(defn get-user-slug
  [content]
  (-> content last first))

(defcomponent data-form
  [{:keys [data content]} owner]
  (display-name [_] "data-form")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch [:form :submit %])
                 :form-message nil
                 :data-name {:value nil
                             :error? true}
                 :url {:value nil
                       :error? true}
                 :description {:value nil
                               :error? true}
                 :data-content (get-data data content)}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    data-content (om/get-state owner :data-content)
                    update-data-name-state (update-field-state [:data-name])
                    update-url-state (update-field-state [:url])
                    update-desc-state (update-field-state [:description])]
                (go-loop []
                  (let [[from meta-info value] (<! ch)]
                    (condp = from
                      :form (condp = meta-info
                              :submit (handle-form-submission owner ch value)
                              :request (if (nil? data-content)
                                         (make-post-request ch
                                                            (get-user-slug
                                                             content)
                                                            value)
                                         (make-put-request ch
                                                           (last content)
                                                           data-content
                                                           value))
                              :response (if (nil? data-content)
                                          (handle-post-response data
                                                                owner
                                                                value)
                                          (handle-put-response data
                                                               owner
                                                               value)))
                      :data-name (update-data-name-state owner meta-info value)
                      :url (update-url-state owner meta-info value)
                      :description (update-desc-state owner
                                                      meta-info
                                                      value)))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   button-node (uq/by-id "submit-button")]
               (ue/listen button-node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      button-node (uq/by-id "submit-button")]
                  (ue/unlisten button-node (:CLICK ue/event-type) callback-fn)))
  (render-state [_ {:keys [ch data-name url description form-message data-content]}]
                [:form {:role "form"}
                 [:fieldset {}
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Name cannot empty."]]
                                    :id :data-name
                                    :title "Name"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:name data-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Url cannot empty."]]
                                    :id :url
                                    :title "Url"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url data-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Description cannot empty."]]
                                    :id :description
                                    :title "Description"
                                    :view textarea-field}
                             :state {:value (:description data-content)}})]
                 [:footer {}
                  (om/build button
                            {:content "Submit"}
                            {:opts {:id "submit-button"}
                             :state {:disabled? (or (:error? data-name)
                                                    (:error? url)
                                                    (:error? description))
                                     :classes "btn-primary"}})
                  (when-not (nil? form-message)
                    [:div {}
                     [:ul {:class "list-unstyled"}
                      [:li {}
                       form-message]]])]]))
