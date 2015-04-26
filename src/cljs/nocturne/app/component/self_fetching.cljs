(ns cljs.nocturne.app.component.self-fetching
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.nocturne.util.auth :only [fetch-self!]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn handle-fetching-self
  [self owner response]
  (om/set-state! owner :ok? true)
  (om/update! self (-> response :cookies :id)))

(defcomponent self-fetching
  [{:keys [self]} owner {:keys [view]}]
  (display-name [_] "self-fetching")
  (init-state [_]
              {:ok? (not (empty? self))})
  (will-mount [_]
              (when-not (om/get-state owner :ok?)
                (let [ch (fetch-self!)]
                  (go-loop []
                    (let [[response-type response] (<! ch)]
                      (if (= response-type :ok)
                        (handle-fetching-self self owner response)
                        (fetch-self! ch)))
                    (recur)))))
  (render-state [_ {:keys [ok?]}]
                (when ok?
                  (om/build view {:self self}))))
