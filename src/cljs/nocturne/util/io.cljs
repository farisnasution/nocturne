(ns cljs.nocturne.util.io
  (:require [ajax.core :as ajax])
  (:use [cljs.core.async :only [put! chan]]))

(defn external-ajax-config
  [ch params headers]
  (cond-> {:handler #(put! ch [:ok %])
           :error-handler #(put! ch [:fail %])
           :response-format :json
           :format :json
           :keywords? true}
          (map? params) (assoc :params params)
          (map? headers) (assoc :headers headers)))

(defn ajax-config
  [ch params headers]
  (cond-> {:handler #(put! ch [:ok %])
           :error-handler #(put! ch [:fail %])
           :response-format :transit
           :format :transit}
          (map? params) (assoc :params params)
          (map? headers) (assoc :headers headers)))

(defn base-get-request
  [config-fn]
  (fn [url {:keys [params headers]} & [channel]]
    (let [ch (if (nil? channel)
               (chan)
               channel)
          config (config-fn ch params headers)]
      (do
        (ajax/GET url config)
        ch))))

(def get-request (base-get-request ajax-config))

(def external-get-request (base-get-request external-ajax-config))

(defn post-request
  [url {:keys [params headers]} & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/POST url config)
      ch)))

(defn put-request
  [url {:keys [params headers]} & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/PUT url config)
      ch)))

(defn delete-request
  [url {:keys [params headers]} & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/DELETE url config)
      ch)))
