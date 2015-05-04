(ns cljs.nocturne.app.d3-chart.donut-pie.layout
  (:use [cljs.nocturne.util.generic :only [not-nil?]]))

(defn donut-pie-layout
  [{:keys [value sort-fn start-angle end-angle pad-angle]}]
  (cond-> (-> js/d3 .-layout .pie)
          (not-nil? value) (.value value)
          (not-nil? sort-fn) (.sort sort-fn)
          (not-nil? start-angle) (.startAngle start-angle)
          (not-nil? end-angle) (.endAngle end-angle)
          (not-nil? pad-angle) (.padAngle pad-angle)))
