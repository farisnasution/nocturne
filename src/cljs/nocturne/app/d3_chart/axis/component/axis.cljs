(ns cljs.nocturne.app.d3-chart.axis.component.axis
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use [cljs.nocturne.app.d3-chart.axis.component.ordinal-tick :only
         [ordinal-ticks]]
        [cljs.nocturne.app.d3-chart.axis.component.numerical-tick :only
         [numerical-ticks]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defn construct-line-axis
  [line-axis {:keys [size margin]} orient]
  (merge {:stroke "black"
          :show? true}
         (cond
          (or (= orient :top)
              (= orient :bottom)) {:x2 (- (:width size)
                                          (:left margin)
                                          (:right margin))}
          (or (= orient :left)
              (= orient :right)) {:y2 (- (:height size)
                                         (:top margin)
                                         (:bottom margin))})
         line-axis))

(defn construct-text-legend
  [end-text-opts orient]
  (merge (condp = orient
           :top {:x -6
                 :dx "-0.71em"
                 :text-anchor "middle"}
           :bottom {:x 6
                    :dx "0.71em"
                    :text-anchor "middle"}
           :left {:transform "rotate(-90)"
                  :y 6
                  :dy "0.71em"
                  :text-anchor "end"}
           :right {:transform "rotate(-90)"
                   :y -6
                   :dy "-0.71em"
                   :text-anchor "end"})
         {:show? true}
         end-text-opts))

(defn construct-outer-g
  [{:keys [size margin]} orient]
  (condp = orient
    :top {:x (:left margin)
          :y (:top margin)}
    :bottom {:x (:left margin)
             :y (- (:height size)
                   (:bottom margin))}
    :left {:x (:left margin)
           :y (:top margin)}
    :right {:x (- (:width size)
                  (:right margin))
            :y (:top margin)}))

(defcomponent ordinal-axis
  [{:keys [size scale-fn opts orient data]} owner]
  (display-name [_] "ordinal-axis")
  (render [_]
          [:g {:transform (-> size
                              (construct-outer-g orient)
                              adu/translate)}
           (om/build ordinal-ticks
                     {:scale-fn scale-fn
                      :orient orient
                      :opts (select-keys opts [:line :text])
                      :data data}
                     {:react-key "ordinal-ticks"})
           (let [text-legend-opts (-> opts
                                      :text-legend
                                      (construct-text-legend orient))]
             (when (-> text-legend-opts :show? true?)
               (om/build ads/text
                         text-legend-opts
                         {:react-key "text-legend"})))
           (let [line-axis-opts (-> opts
                                    :line-axis
                                    (construct-line-axis size orient))]
             (when (-> line-axis-opts :show? true?)
               (om/build ads/line
                         line-axis-opts
                         {:react-key "line-axis"})))]))

(defcomponent numerical-axis
  [{:keys [size scale-fn opts orient data]} owner]
  (display-name [_] "numerical-axis")
  (render [_]
          [:g {:transform (-> size
                              (construct-outer-g orient)
                              adu/translate)}
           (om/build numerical-ticks
                     {:scale-fn scale-fn
                      :orient orient
                      :opts (select-keys opts [:line :text :tick])
                      :data data}
                     {:react-key "numerical-ticks"})
           (let [text-legend-opts (-> opts
                                      :text-legend
                                      (construct-text-legend orient))]
             (when (-> text-legend-opts :show? true?)
               (om/build ads/text
                         text-legend-opts
                         {:react-key "text-legend"})))
           (let [line-axis-opts (-> opts
                                    :line-axis
                                    (construct-line-axis size orient))]
             (when (-> line-axis-opts :show? true?)
               (om/build ads/line
                         line-axis-opts
                         {:react-key "line-axis"})))]))
