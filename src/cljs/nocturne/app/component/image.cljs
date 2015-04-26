(ns cljs.nocturne.app.component.image
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]))

(defcomponent image
  [{:keys [image-url]} owner]
  (display-name [_] "image")
  (render [_]
          [:img {:class "image-responsive"
                 :src image-url}]))
