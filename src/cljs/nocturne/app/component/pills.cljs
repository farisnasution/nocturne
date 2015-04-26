(ns cljs.nocturne.app.component.pills
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.query :as uq]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.app.component.anchor :as aca])
  (:use [cljs.nocturne.app.user.icon-name :only [overview-icon]]
        [cljs.nocturne.app.data.icon-name :only [data-icon]]
        [cljs.nocturne.app.chart.icon-name :only [chart-icon]]
        [cljs.nocturne.app.showcase.icon-name :only [showcase-icon]]
        [cljs.nocturne.app.user.route :only [user-overview]]
        [cljs.nocturne.app.data.route :only [show-all-data]]
        [cljs.nocturne.app.chart.route :only [show-all-chart]]
        [cljs.nocturne.app.showcase.route :only [show-all-showcase]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]
               [cljs.nocturne.macro :only [defcomponent]]))

(defn redirect-callback
  [e]
  (-> e ue/event->path ur/dispatch!))

(defn same-id?
  [content option-id]
  (= option-id (first content)))

(defn ->user-slug
  [content]
  (-> content last first))

(defcomponent pills-option
  [{:keys [content]} owner {:keys [url-fn
                                   text
                                   option-id
                                   icon-name
                                   extra]}]
  (display-name [_] "pills-option")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn (fn [e]
                                (ue/prevent-default e)
                                (put! ch e))}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [e (<! ch)]
                    (redirect-callback e))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   a-node (-> owner om/get-node uq/first-child)]
               (ue/listen a-node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      a-node (-> owner om/get-node uq/first-child)]
                  (ue/unlisten a-node (:CLICK ue/event-type) callback-fn)))
  (render [_]
          [:li {:role "presentation"
                :class (if (same-id? content option-id) "active" "")}
           (om/build aca/iconed-anchor
                     {:content text
                      :url (url-fn {:user-slug (->user-slug content)})
                      :icon-name icon-name}
                     {:opts {:extra extra}})]))

(defcomponent pills
  [{:keys [content]} owner]
  (display-name [_] "pills")
  (render [_]
          [:ul {:class "nav nav-pills"}
           (om/build pills-option
                     {:content content}
                     {:opts {:url-fn user-overview
                             :text "Overview"
                             :option-id :overview
                             :icon-name overview-icon}
                      :react-key "overview-pills-option"})
           (om/build pills-option
                     {:content content}
                     {:opts {:url-fn show-all-data
                             :text "Data"
                             :option-id :data
                             :icon-name data-icon}
                      :react-key "data-pills-option"})
           (om/build pills-option
                     {:content content}
                     {:opts {:url-fn show-all-chart
                             :text "Chart"
                             :option-id :chart
                             :icon-name chart-icon}
                      :react-key "chart-pills-option"})
           (om/build pills-option
                     {:content content}
                     {:opts {:url-fn show-all-showcase
                             :text "Showcase"
                             :option-id :showcase
                             :icon-name showcase-icon}
                      :react-key "showcase-pills-option"})]))
