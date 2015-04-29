(ns cljs.nocturne.util.math
  (:require [goog.math :as g-math]))

(defn ->pos
  [n]
  (.abs js/Math n))

(defn max-value
  [xs]
  (apply max xs))

(defn min-value
  [xs]
  (let [d (apply min xs)]
    (if (neg? d)
      d
      0)))

(defn floor
  ([data opt-epsilon]
   (g-math/safeFloor data opt-epsilon))
  ([data]
   (g-math/safeFloor data)))

(defn ceil
  ([data opt-epsilon]
   (g-math/safeCeil data opt-epsilon))
  ([data]
   (g-math/safeCeil data)))
