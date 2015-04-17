(ns cljs.nocturne.util.query
  (:require [goog.dom :as dom])
  (:use [clojure.walk :only [keywordize-keys]]))

(defn by-id
  [id]
  (dom/getElement id))

(defn first-child
  [el]
  (dom/getFirstElementChild el))

(defn last-child
  [el]
  (dom/getLastElementChild el))
