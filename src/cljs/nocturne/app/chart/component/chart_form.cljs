(ns cljs.nocturne.app.chart.component.chart-form
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq]
            [cljs.nocturne.util.state :as us]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.app.chart.io :as aci]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.validated :only [validated-field]]
        [cljs.nocturne.app.component.button :only [button]]
        [cljs.nocturne.app.chart.route :only [show-single-chart]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defn handle-form-submission
  [owner ch _]
  (let [chart-name-error (om/get-state owner [:chart-name :error?])
        chart-type-error (om/get-state owner [:chart-type :error?])
        data-slug-error  (om/get-state owner [:data-slug :error?])
        measure-ks-error (om/get-state owner [:measure :ks :error?])
        measure-label-error (om/get-state owner [:measure :label :error?])
        dimension-ks-error (om/get-state owner [:dimension :ks :error?])
        dimension-label-error (om/get-state owner [:dimension :label :error?])]
    (when (and (false? chart-name-error)
               (false? chart-type-error)
               (false? data-slug-error)
               (false? measure-ks-error)
               (false? measure-label-error)
               (false? dimension-ks-error)
               (false? dimension-label-error))
      (let [chart-name (om/get-state owner [:chart-name :value])
            chart-type (om/get-state owner [:chart-type :value])
            data-slug (om/get-state owner [:data-slug :value])
            measure-ks (om/get-state owner [:measure :ks :value])
            measure-label (om/get-state owner [:measure :label :value])
            dimension-ks (om/get-state owner [:dimension :ks :value])
            dimension-label (om/get-state owner [:dimension :label :value])]
        (put! ch [:form :atom [chart-name
                               chart-type
                               data-slug
                               measure-ks
                               measure-label
                               dimension-ks
                               dimension-label]])))))

(defn make-post-request
  [ch user-slug [chart-name
                 chart-type
                 data-slug
                 measure-ks
                 measure-label
                 dimension-ks
                 dimension-label]]
  (go
    (let [v (<! (aci/request-post-chart user-slug
                                       {:name chart-name
                                        :type chart-type
                                        :data-slug data-slug
                                        :measure {:ks measure-ks
                                                  :label measure-label}
                                        :dimension {:ks dimension-ks
                                                    :label dimension-label}}
                                       {}))]
      (put! ch [:form :response v]))))

(defn make-put-request
  [ch [user-slug chart-slug] chart-content [chart-name
                                            chart-type
                                            data-slug
                                            measure-ks
                                            measure-label
                                            dimension-ks
                                            dimension-label]]
  (go
    (let [new-value (into chart-content
                          {:name chart-name
                           :type chart-type
                           :data-slug data-slug
                           :measure {:ks measure-ks
                                     :label measure-label}
                           :dimension {:ks dimension-ks
                                       :label dimension-label}})
          v (<! (aci/request-put-chart user-slug
                                       chart-slug
                                       new-value
                                       {}))]
      (put! ch [:form :response [v new-value]]))))

(defn handle-post-response
  [data owner [response-type response]]
  (if (= response-type :ok)
    (let [chart-slug (:slug response)
          next-path (show-single-chart {:user-slug (:creator-slug response)
                                        :chart-slug chart-slug})
          _ (om/transact! data (fn [current]
                                 (assoc current chart-slug response)))]
      (ur/dispatch! (us/get-history) next-path))
    (om/set-state! owner :form-message response)))

(defn handle-put-response
  [data owner [response-type response new-value]]
  (if (= response-type :ok)
    (let [chart-slug (:slug new-value)
          next-path (show-single-chart {:user-slug (:creator-slug new-value)
                                        :chart-slug chart-slug})
          _ (om/transact! data (fn [current]
                                 (assoc current chart-slug new-value)))]
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

(defcomponent chart-form
  [{:keys [data content]} owner]
  (display-name [_] "chart-form")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch [:form :submit %])
                 :form-message nil
                 :chart-name {:error? false
                              :value nil}
                 :chart-type {:error? false
                              :value nil}
                 :data-slug {:error? false
                             :value nil}
                 :measure {:label {:error? false
                                   :value nil}
                           :ks {:error? false
                                :value nil}}
                 :dimension {:label {:error? false
                                     :value nil}
                             :ks {:error? false
                                  :value nil}}
                 :chart-content (get-data data content)}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    chart-content (om/get-state owner :chart-content)
                    update-chart-name-state (update-field-state [:chart-name])
                    update-chart-type-state (update-field-state [:chart-type])
                    update-data-slug-state (update-field-state [:data-slug])
                    update-measure-ks-state (update-field-state [:measure :ks])
                    update-measure-label-state (update-field-state
                                                [:measure :label])
                    update-dimension-ks-state (update-field-state
                                               [:dimension :ks])
                    update-dimension-label-state (update-field-state
                                                  [:dimension :label])]
                (go-loop []
                  (let [[from meta-info value] (<! ch)]
                    (condp = from
                      :form (condp = meta-info
                              :submit (handle-form-submission owner ch value)
                              :request (if (nil? chart-content)
                                         (make-post-request ch
                                                            (get-user-slug
                                                             content)
                                                            value)
                                         (make-put-request ch
                                                           (last content)
                                                           chart-content
                                                           value))
                              :response (if (nil? chart-content)
                                          (handle-post-response data
                                                                owner
                                                                value)
                                          (handle-put-response data
                                                               owner
                                                               value)))
                      :chart-name (update-chart-name-state owner meta-info value)
                      :chart-type (update-chart-type-state owner meta-info value)
                      :data-slug (update-data-slug-state owner
                                                         meta-info
                                                         value)
                      :measure-ks (update-measure-ks-state owner meta-info value)
                      :measure-label (update-measure-label-state owner
                                                                 meta-info
                                                                 value)
                      :dimension-ks (update-dimension-ks-state owner
                                                               meta-info
                                                               value)
                      :dimension-label (update-dimension-label-state owner
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
  (render-state [_ {:keys [ch
                           chart-name
                           chart-type
                           data-slug
                           measure
                           dimension
                           form-message
                           chart-content]}]
                [:form {:role "form"}
                 [:fieldset {}
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Name cannot empty."]]
                                    :id :chart-name
                                    :title "Name"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:name chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Type cannot empty."]]
                                    :id :chart-type
                                    :title "Type"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Data slug cannot empty."]]
                                    :id :data-slug
                                    :title "Data slug"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Measure value cannot empty."]]
                                    :id :measure-ks
                                    :title "Measure value"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Measure label cannot empty."]]
                                    :id :measure-label
                                    :title "Measure label"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Dimension value cannot empty."]]
                                    :id :dimension-ks
                                    :title "Dimension value"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :state {:value (:url chart-content)}})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Dimension label cannot empty."]]
                                    :id :dimension-label
                                    :title "Description"
                                    :view input-field}
                             :state {:value (:description chart-content)}})]
                 [:footer {}
                  (om/build button
                            {:content "Submit"}
                            {:opts {:id "submit-button"}
                             :state {:disabled? (or (:error? chart-name)
                                                    (:error? chart-type)
                                                    (:error? data-slug)
                                                    (-> measure :ks :error?)
                                                    (-> measure :label :error?)
                                                    (-> dimension :ks :error?)
                                                    (-> dimension :label :error?))
                                     :classes "btn-primary"}})
                  (when-not (nil? form-message)
                    [:div {}
                     [:ul {:class "list-unstyled"}
                      [:li {}
                       form-message]]])]]))
