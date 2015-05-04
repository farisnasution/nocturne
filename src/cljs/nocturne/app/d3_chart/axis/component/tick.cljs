(ns cljs.nocturne.app.d3-chart.axis.component.tick
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.d3-chart.shape :as ads]
            [cljs.nocturne.app.d3-chart.util :as adu])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent base-tick
  [{:keys [g line text]} owner]
  (display-name [_] "base-tick")
  (render [_]
          [:g {:transform (adu/translate g)}
           (om/build ads/line line)
           (when (-> text :show? true?)
             (om/build ads/text text))]))

(defn construct-line
  [line-opts orient]
  (merge (condp = orient
           :top {:y2 "-0.5em"}
           :bottom {:y2 "0.5em"}
           :left {:x2 "-6"}
           :right {:x2 "6"}
           {:x2 "-6"})
         {:stroke "black"}
         line-opts))

(defn construct-text
  [text-opts orient data]
  (merge {:show? true
          :content data}
         (condp = orient
           :top {:dy "-1.4em"
                 :text-anchor "middle"}
           :bottom {:dy "1.4em"
                    :text-anchor "middle"}
           :left {:dx "-1.4em"
                  :text-anchor "end"}
           :right {:dx "1.4em"
                   :text-anchor "start"}
           {:dx "-1.4em"
            :text-anchor "end"})
         text-opts))

(defcomponent tick
  [{:keys [g orient opts data]} owner]
  (display-name [_] "tick")
  (render [_]
          (om/build base-tick
                    {:g g
                     :line (construct-line (:line opts) orient)
                     :text (construct-text (:text opts) orient data)})))
