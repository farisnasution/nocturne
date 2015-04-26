(ns cljs.nocturne.app.component.login-form
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq]
            [cljs.nocturne.util.io :as ui]
            [cljs.nocturne.util.path :as up]
            [cljs.nocturne.util.history :as uh]
            [cljs.nocturne.util.state :as us]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.app.component.field.input :only [input-field]]
        [cljs.nocturne.app.component.field.validated :only [validated-field]]
        [cljs.nocturne.app.component.button :only [button]]
        [cljs.nocturne.util.auth :only [authenticate!]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop go]]))

(defn handle-form-submission
  [owner ch _]
  (let [username-error (om/get-state owner [:username :error?])
        password-error (om/get-state owner [:password :error?])]
    (when (and (false? username-error)
               (false? password-error))
      (let [username (om/get-state owner [:username :value])
            password (om/get-state owner [:password :value])]
        (put! ch [:form :request [username password]])))))

(defn make-request
  [ch url [username password]]
  (go
    (let [v (<! (ui/post-request url {:params {:username username
                                               :password password}}))]
      (put! ch [:form :response v]))))

(defn handle-response
  [owner [response-type response]]
  (if (= response-type :ok)
    (let [current-path (up/current-location)
          _ (authenticate! response)]
      (uh/set-token! (us/get-history) current-path))
    (om/set-state! owner :form-message response)))

(defn update-field-state
  [ks]
  (fn [owner error [value _]]
    (om/update-state! owner
                      ks
                      (fn [current]
                        (assoc current :value value
                                       :error? error)))))

(defcomponent login-form
  [data owner {:keys [login-url]}]
  (display-name [_] "login-form")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn (fn [e]
                                (ue/prevent-default e)
                                (put! ch [:form :submit e]))
                 :form-message nil
                 :username {:value nil
                            :error? true}
                 :password {:value nil
                            :error? true}}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)
                    update-username-state (update-field-state [:username])
                    update-password-state (update-field-state [:password])]
                (go-loop []
                  (let [[from meta-info value] (<! ch)]
                    (condp = from
                      :form (condp = meta-info
                              :submit (handle-form-submission owner ch value)
                              :request (make-request ch login-url value)
                              :response (handle-response owner value))
                      :username (update-username-state owner meta-info value)
                      :password (update-password-state owner meta-info value)))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   button-node (uq/by-id "login-button")]
               (ue/listen button-node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      button-node (uq/by-id "login-button")]
                  (ue/unlisten button-node (:CLICK ue/event-type) callback-fn)))
  (render-state [_ {:keys [ch username password form-message]}]
                [:form {:role "form"}
                 [:fieldset {}
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Username cannot empty."]]
                                    :id :username
                                    :title "Title"
                                    :view input-field
                                    :view-opts {:field-type "text"}}
                             :react-key "username-field"})
                  (om/build validated-field
                            {}
                            {:opts {:parent-ch ch
                                    :val-fns [[#(not (empty? %))
                                               "Password cannot empty."]]
                                    :id :password
                                    :title "Password"
                                    :view input-field
                                    :view-opts {:field-type "password"}}
                             :react-key "password-field"})]
                 [:footer {}
                  (om/build button
                            {:content "Login"
                             :disabled (or (:error? username)
                                           (:error? password))}
                            {:opts {:btn-type "primary"
                                    :id "login-button"}})
                  (when-not (nil? form-message)
                    [:div {}
                     [:ul {:class "list-unstyled"}
                      [:li {}
                       form-message]]])]]))
