(ns cljs.nocturne.app.d3-chart.bubble.layout
  (:use [cljs.nocturne.util.generic :only [not-nil?]]))

(defn -invoke-sort
  [scale value]
  (.sort scale (if (false? value) nil value)))

(defn -invoke-size
  [scale value]
  (.size scale (apply array value)))

(defn bubble-layout
  [{:keys [value sort-fn size radius padding children]}]
  (cond-> (.pack (.-layout js/d3))
          (not-nil? value) (.value value)
          (not-nil? sort-fn) (-invoke-sort sort-fn)
          (not-nil? size) (-invoke-size size)
          (not-nil? radius) (.radius radius)
          (not-nil? padding) (.padding padding)
          (not-nil? children) (.children children)))
