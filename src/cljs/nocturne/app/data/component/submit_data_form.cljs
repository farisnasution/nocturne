(ns cljs.nocturne.app.data.component.submit-data-form
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.textarea :only [textarea-field]]
        [cljs.nocturne.app.component.field.validated :only [validated-field]]
        [cljs.nocturne.app.component.button :only [button]]
        [cljs.nocturne.app.data.route :only [show-single-data]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

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
            description] (om/get-state owner [:description :value])
        (put! ch [:form :request [data-name url description]])))))

(defn make-request
  [ch url [data-name data-url description]]
  (go
    (let [v (<! (ui/post-request url {:params {:name data-name
                                               :url data-url
                                               :description description}}))]
      (put! ch [:form :response v]))))

(defn handle-response
  "TODO: shud not be doing io here, instead do transact! and then abuse :tx-listen"
  [owner [response-type response]]
  (if (= response-type :ok)
    (let [next-path (show-single-data {:user-slug (:creator-slug response)
                                       :data-slug (:slug response)})]
      (uh/set-token! (us/get-history) next-path))
    (om/set-state! owner :form-message response)))

(defn update-field-state
  [ks]
  (fn [owner error [value _]]
    (om/update-state! owner
                      ks
                      (fn [current]
                        (assoc current :value value
                                       :error? error)))))

(defcomponent submit-data-form
  "TODO: val-fns for each field"
  [data owner {:keys [submit-url]}]
  (display-name [_] "submit-data-form")
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
                               :error? true}}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    update-data-name-state (update-field-state [:data-name])
                    update-url-state (update-field-state [:url])
                    update-desc-state (update-field-state [:description])]
                (go-loop []
                  (let [[from meta-info value] (<! ch)]
                    (condp = from
                      :form (condp = meta-info
                              :submit (handle-form-submission owner ch value)
                              :request (make-request ch submit-url value)
                              :response (handle-response owner value))
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
  (render-state [_ {:keys [ch data-name url description form-message]}]
                [:form {:role "form"}
                 [:fieldset {}
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns
                                    :id :data-name
                                    :title "Name"
                                    :view input-field
                                    :view-opts {:field-type "text"}}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns
                                    :id :url
                                    :title "Url"
                                    :view input-field
                                    :view-opts {:field-type "text"}}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns
                                    :id :description
                                    :title "Description"
                                    :view textarea-field}})]
                 [:footer {}
                  (om/build button
                            {:content "Submit"
                             :disabled (or (:error?  data-name)
                                           (:error? url)
                                           (:error? description))}
                            {:opts {:btn-type "primary"
                                    :id "submit-button"}})
                  (when-not (nil? form-message)
                    [:div {}
                     [:ul {:class "list-unstyled"}
                      [:li {}
                       form-message]]])]]))
