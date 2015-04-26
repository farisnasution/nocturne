(ns cljs.nocturne.app.showcase.component.table
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.nocturne.util.event :as ue]
            [cljs.nocturne.util.data :as ud])
  (:use [cljs.nocturne.app.showcase.component.overview-table :only [tbody
                                                                    th]])
  (:use-macros [cljs.nocturne.macro :only [defcomponent]]
               [cljs.core.async.macros :only [go-loop]]))

(defn update-order
  [o]
  (cond
   (nil? order) :ascending
   (= order :ascending) :descending
   (= order :descending) nil))

(defcomponent ordering-th
  [{:keys [content]} owner {:keys [table-ch
                                   sort-ks]}]
  (display-name [_] "ordering-th")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn #(put! ch %)
                 :order nil}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [_ (<! ch)]
                    (om/update-state! owner [:order] update-order))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node (:CLICK ue/event-type) callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node (:CLICK ue/event-type) callback-fn)))
  (did-update [_ prev-props prev-state]
              (let [order (om/get-state owner :order)]
                (put! table-ch [sort-ks order])))
  (render [_]
          [:th {}
           content]))

(defcomponent thead
  [data owner {:keys [table-ch]}]
  (display-name [_] "thead")
  (render [_]
          [:thead {}
           (om/build ordering-th
                     {:content "Name"}
                     {:opts {:table-ch table-ch
                             :sort-ks [:name]}
                      :react-key "showcase-th-name"})
           (om/build th
                     {:content "Charts"}
                     {:react-key "showcase-th-charts"})
           (om/build ordering-th
                     {:content "Date"}
                     {:opts {:table-ch table-ch
                             :sort-ks [:date]}
                      :react-key "showcase-th-date"})]))

(defn order-data
  [by order data]
  (cond
   (nil? order) data
   (= order :ascending) (ud/ascend-by-keys by data)
   (= order :descending) (ud/descend-by-keys by data)))

(defcomponent data-table
  [data owner]
  (display-name [_] "table")
  (init-state [_]
              {:ch (chan)
               :by nil
               :order nil})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [[ks order] (<! ch)]
                    (om/update-state! owner #(assoc % :by ks :order order)))
                  (recur))))
  (render-state [_ {:keys [ch by order]}]
                [:div {:class "table-responsive"}
                 [:table {:class "table"}
                  (om/build thead
                            {}
                            {:opts {:table-ch ch}
                             :react-key "showcase-thead"})
                  (om/build tbody
                            (order-data by order data)
                            {:react-key "showcase-tbody"})]]))
