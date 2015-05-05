(ns cljs.nocturne.app.component.navbar
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.nocturne.app.component.anchor :as aca]
            [arosequist.om-autocomplete :as ac]
            [arosequist.om-autocomplete.bootstrap :as acb]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.app.user.io :as aui]
            [cljs.core.async :as async :refer [<! put! chan timeout]])
  (:use [cljs.nocturne.app.user.icon-name :only [overview-icon
                                                 settings-icon
                                                 logout-icon]]
        [cljs.nocturne.app.data.icon-name :only [data-icon]]
        [cljs.nocturne.app.chart.icon-name :only [chart-icon]]
        [cljs.nocturne.app.showcase.icon-name :only [showcase-icon]]
        [cljs.nocturne.app.user.route :only [user-overview
                                             user-settings
                                             user-logout]]
        [cljs.nocturne.app.data.route :only [show-all-data]]
        [cljs.nocturne.app.chart.route :only [show-all-chart]]
        [cljs.nocturne.app.showcase.route :only [show-all-showcase]])
  (:use-macros [cljs.core.async.macros :only [go-loop go]]
               [cljs.nocturne.macro :only [defcomponent]]))

(defn suggestion-fn
  [value suggestions-ch _]
  (go
    (<! (timeout 500))
    (let [result-ch (aui/request-get-user {:params value})
          result (<! result-ch)]
      (put! suggestions-ch result))))

(defn result-text-fn
  [item idx]
  (str item))

(defn handle-result
  [[idx result]]
  (ur/dispatch! (user-overview {:user-slug (:slug result)})))

(defcomponent search-field
  [data owner]
  (display-name [_] "search-field")
  (init-state [_]
              {:ch (chan)})
  (will-mount [_]
              (let [ch (om/get-state owner [:ch])]
                (go-loop []
                  (let [result (<! ch)]
                    (handle-result result))
                  (recur))))
  (render-state [_ {:keys [ch]}]
                (om/build ac/autocomplete
                          nil
                          (acb/add-bootstrap-m
                           {:opts {:input-opts {:placeholder "search"
                                                :class "form-control"
                                                :type "text"}
                                   :result-ch ch
                                   :result-text-fn result-text-fn
                                   :suggestions-fn suggestion-fn}}))))

(defcomponent navbar-option
  [data owner {:keys [url-fn
                      url
                      icon-name]}]
  (display-name [_] "navbar-option")
  (render [_]
          [:li {}
           (om/build aca/iconed-anchor
                     {:url (if url
                             url
                             (url-fn {:user-slug (:slug data)}))
                      :icon-name icon-name})]))

(defcomponent navbar
  [{:keys [self]} owner]
  (display-name [_] "navbar")
  (render [_]
          [:nav {:class "navbar navbar-default"}
           [:div {:class "container-fluid"}
            [:div {:class "navbar-header"}
             [:button {:type "button"
                       :class "navbar-toggle collapsed"
                       :data-toggle "collapse"
                       :data-target "#bs-example-navbar-collapse-1"}
              [:span {:class "sr-only"}
               "Toggle Navigation"]
              [:span {:class "icon-bar"}]
              [:span {:class "icon-bar"}]
              [:span {:class "icon-bar"}]]
             [:a {:class "navbar-brand"
                  :href "#"}
              "Brand"]]
            [:div {:class "collapse navbar-collapse"
                   :id "bs-example-navbar-collapse-1"}
             [:form {:class "navbar-form navbar-left"
                     :role "search"}
              [:div {:class "form-group"}
               (om/build search-field {})]]
             [:ul {:class "nav navbar-nav navbar-right"}
              (om/build navbar-option
                        self
                        {:opts {:url-fn user-overview
                                :icon-name overview-icon}
                         :react-key "overview-navbar-option"})
              (om/build navbar-option
                        self
                        {:opts {:url-fn show-all-data
                                :icon-name data-icon}
                         :react-key "data-navbar-option"})
              (om/build navbar-option
                        self
                        {:opts {:url-fn show-all-chart
                                :icon-name chart-icon}
                         :react-key "chart-navbar-option"})
              (om/build navbar-option
                        self
                        {:opts {:url-fn show-all-showcase
                                :icon-name showcase-icon}
                         :react-key "showcase-navbar-option"})
              (om/build navbar-option
                        self
                        {:opts {:url (user-settings)
                                :icon-name settings-icon}
                         :react-key "settings-navbar-option"})
              (om/build navbar-option
                        self
                        {:opts {:url (user-logout)
                                :icon-name logout-icon}
                         :react-key "logout-navbar-option"})]]]]))
