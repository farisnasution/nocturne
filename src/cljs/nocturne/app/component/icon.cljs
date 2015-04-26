(ns cljs.nocturne.app.component.icon
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [clojure.string :as string])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent icon
  [{:keys [icon-name]} owner {:keys [extra]}]
  (display-name [_] "icon")
  (render [_]
          [:i {:class (->> ["fa" icon-name]
                           (concat extra)
                           (string/join " "))}]))
