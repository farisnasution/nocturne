(ns cljs.nocturne.util.events
  (:require [goog.events :as ev])
  (:use [clojure.walk :only [keywordize-keys]]))

(def event-type (-> ev/EventType
                    js->clj
                    keywordize-keys))

(defn listen
  ([container events callback-fn]
   (ev/listen container events callback-fn))
  ([container events callback-fn opt-capt]
   (ev/listen container events callback-fn opt-capt))
  ([container events callback-fn opt-capt opt-handler]
   (ev/listen container events callback-fn opt-capt opt-handler)))

(defn unlisten
  ([container events callback-fn]
   (ev/unlisten container events callback-fn))
  ([container events callback-fn opt-capt]
   (ev/unlisten container events callback-fn opt-capt))
  ([container events callback-fn opt-capt opt-handler]
   (ev/unlisten container events callback-fn opt-capt opt-handler)))

(defn prevent-default
  [event]
  (.preventDefault event))

(defn event->path
  [event]
  (-> event .-currentTarget .-pathname))
