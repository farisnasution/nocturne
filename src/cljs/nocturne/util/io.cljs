(ns cljs.nocturne.util.io
  (:require [ajax.core :as ajax])
  (:use [cljs.core.async :only [put! chan]]))

(defn ajax-config
  [ch params headers]
  (cond-> {:handler #(put! ch [:ok %])
           :error-handler #(put! ch [:fail %])
           :response-format :transit}
          (map? params) (assoc :params params)
          (map? headers) (assoc :headers headers)))

(defn get-request
  [url [{:keys [params headers]}] & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/GET url config)
      ch)))

(defn post-request
  [url [{:keys [params headers]}] & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/POST url config)
      ch)))

(defn put-request
  [url [{:keys [params headers]}] & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/PUT url config)
      ch)))

(defn delete-request
  [url [{:keys [params headers]}] & [channel]]
  (let [ch (if (nil? channel)
             (chan)
             channel)
        config (ajax-config ch params headers)]
    (do
      (ajax/DELETE url config)
      ch)))
