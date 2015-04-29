(ns cljs.nocturne.util.event
  (:require [goog.events :as ev])
  (:use [clojure.walk :only [keywordize-keys]])
  (:import [goog.events KeyCodes EventType]))

(def event-type (-> EventType
                    js->clj
                    keywordize-keys))

(def keycodes (-> KeyCodes
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

(defn keycode
  [event]
  (.-keyCode event))

(defn prevent-default
  [event]
  (.preventDefault event))

(defn event->path
  [event]
  (-> event .-currentTarget .-pathname))

(defn event->value
  [event]
  (-> event .-currentTarget .-value))

(defn event->id
  [event]
  (-> event .-currentTarget .-id))
