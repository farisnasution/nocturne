(ns cljs.nocturne.app.d3-chart.donut-pie.construct
  (:use [cljs.nocturne.util.generic :only [not-nil?]]))

(defn donut-pie-constructor
  [{:keys [inner-radius outer-radius
           corner-radius pad-radius
           start-angle end-angle
           pad-angle]}]
  (cond-> (-> js/d3 .-svg .arc)
          (not-nil? inner-radius) (.innerRadius inner-radius)
          (not-nil? outer-radius) (.outerRadius outer-radius)
          (not-nil? corner-radius) (.cornerRadius corner-radius)
          (not-nil? pad-radius) (.padRadius pad-radius)
          (not-nil? start-angle) (.startAngle start-angle)
          (not-nil? end-angle) (.endAngle end-angle)
          (not-nil? pad-angle) (.padAngle pad-angle)))
