(ns cljs.nocturne.app.component.self-fetching
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.util.auth :only [self-request!]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn get-user-from-self
  [data]
  (let [users (:users data)
        self (:self data)]
    (get users self)))

(defn handle-fetching-self
  [data owner [response-type response]]
  (when (= response-type :ok)
    (do
      (om/set-state! owner :ok? true)
      (om/transact! data (fn [current]
                           (-> current
                               (assoc :self (:slug response))
                               (update-in [:users]
                                          (fn [users r]
                                            (into users r))
                                          response)))))))

(defcomponent self-fetching
  [data owner {:keys [view]}]
  (display-name [_] "self-fetching")
  (init-state [_]
              {:ok? (-> (:self data)
                        nil?
                        not)})
  (will-mount [_]
              (when-not (om/get-state owner :ok?)
                (let [ch (self-request!)]
                  (go-loop []
                    (let [[response-type response] (<! ch)]
                      (if (= response-type :ok)
                        (handle-fetching-self data owner response)
                        (self-request! ch)))
                    (recur)))))
  (render-state [_ {:keys [ok?]}]
                (when ok?
                  (om/build view {:user (get-user-from-self data)}))))
