(ns cljs.nocturne.app.component.button
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent button
  [{:keys [content]} owner {:keys [classes id]}]
  (display-name [_] "button")
  (init-state [_]
              {:disabled? false})
  (render-state [_ {:keys [disabled?]}]
                [:button {:style {:border-radius 0}
                          :class (str "btn "
                                      classes)
                          :type "submit"
                          :disabled disabled?
                          :id id}
                 content]))
