(ns cljs.nocturne.app.component.field.select
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent select-value
  [{:keys [text opts]} owner]
  (display-name [_] "select-value")
  (render [_]
          [:option (select-keys opts [:disabled
                                      :label
                                      :selected
                                      :value])
           text]))

(defcomponent select
  [{:keys [data opts]} owner]
  (display-name [_] "select")
  (render [_]
          [:select (select-keys opts [:autofocus
                                      :disabled
                                      :form
                                      :multiple
                                      :name
                                      :required
                                      :size])
           (om/build-all select-value
                         (map-indexed (fn [idx d]
                                        (assoc d :react-key idx))
                                      data)
                         {:key :react-key})]))
