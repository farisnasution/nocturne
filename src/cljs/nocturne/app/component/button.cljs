(ns cljs.nocturne.app.component.button
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent button
  [{:keys [content disabled]} owner {:keys [btn-type id]}]
  (display-name [_] "button")
  (render [_]
          [:button {:style {:border-radius 0}
                    :class (str "btn"
                                (when btn-type
                                  (str " btn-" btn-type)))
                    :type "submit"
                    :disabled (when-not (nil? disabled)
                                (boolean disabled))
                    :id id}
           content]))
