(ns cljs.nocturne.app.d3-chart.line-area.construct
  (:use [cljs.nocturne.util.generic :only [not-nil?]]))

(defn area-constructor
  [{:keys [x y
           x0 x1
           y0 y1
           interpolate tension
           defined]}]
  (cond-> (-> js/d3 .-svg .area)
          (not-nil? x) (.x x)
          (not-nil? y) (.y y)
          (not-nil? x0) (.x0 x0)
          (not-nil? x1) (.x1 x1)
          (not-nil? y0) (.y0 y0)
          (not-nil? y1) (.y1 y1)
          (not-nil? interpolate) (.interpolate interpolate)
          (not-nil? tension) (.tension tension)
          (not-nil? defined) (.defined defined)))

(defn line-constructor
  [{:keys [interpolation x y
           tension defined radius
           angle]}]
  (cond-> (-> js/d3 .-svg .line)
          (not-nil? interpolation) (.interpolate interpolation)
          (not-nil? x) (.x x)
          (not-nil? y) (.y y)
          (not-nil? tension) (.tension tension)
          (not-nil? defined) (.defined defined)))
