(ns clj.nocturne.app.chart.resource
  (:require [clj.nocturne.app.chart.validator :as adv]
            [clj.nocturne.app.chart.mapper :as adm]
            [clj.nocturne.app.base.util :as abu]
            [clj.nocturne.app.base.resource :as abr]
            [monger.collection :as mc]
            [monger.query :as mq])
  (:use [liberator.core :only [defresource]]
        [enigma.doc.mapper :only [process]]
        [enigma.doc.validator.core :only [validate]]))

(defn -update-chart
  [chart-slug body]
  (fn [chart]
    (if-not (= chart-slug (:slug chart))
      chart
      (merge chart body))))

(defresource single-chart-resource
  [user-slug chart-slug]
  (merge abr/single-resource
         (abr/ownership-auth-resource #{"user"}
                                      #{:put :delete}
                                      user-slug))
  :handle-ok (fn [{:keys [entity]}]
               (->> entity
                    :chart
                    (filter #(= chart-slug (:slug %)))
                    first))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :put (:request-method request))
                  [false {}]
                  (let [body (-> request
                                 :body
                                 (select-keys [:name :description
                                               :data-slug :chart-type
                                               :dimension :measurement]))
                        error (validate adv/chart-validator body)]
                    (if (nil? error)
                      [false {:body body}]
                      [true {:error error}]))))
  :exists? (fn [{:keys [request body]}]
             (let [chart-updater (-update-chart chart-slug body)
                   entity (-> request
                              :db
                              (mc/find-one-as-map "user"
                                                  {:slug user-slug
                                                   :chart.slug chart-slug}))]
               (if (nil? entity)
                 [false {:error "Entity not found."}]
                 [true {:entity (if (nil? body)
                                  entity
                                  (update-in entity
                                             [:chart]
                                             (fn [ds]
                                               (map chart-updater ds))))}])))
  :put! (fn [{:keys [request entity]}]
          (mc/update-by-id (:db request) "user" (:_id entity) entity))
  :delete! (fn [{:keys [request entity]}]
             (let [result (update-in entity
                                     [:chart]
                                     (fn [ds]
                                       (filter (fn [d]
                                                 (not (= chart-slug (:slug d))))
                                               ds)))]
               (mc/update-by-id (:db request)
                                "user"
                                (:_id entity)
                                result))))

(defresource list-chart-resource
  [user-slug]
  (merge abr/list-resource
         (abr/ownership-auth-resource #{"user"}
                                      #{:post}
                                      user-slug))
  :handle-ok (fn [{:keys [request]}]
               (-> request
                   :db
                   (mc/find-one-as-map "user"
                                       {:slug user-slug}
                                       [:chart])
                   :chart))
  :post! (fn [{:keys [request body]}]
           (let [db-instance (:db request)
                 coll-name "user"
                 new-entity (-> (mc/find-one-as-map db-instance
                                                    coll-name
                                                    {:slug user-slug})
                                (update-in [:chart]
                                           (fn [ds]
                                             (conj ds body))))
                 _ (mc/update-by-id db-instance
                                    coll-name
                                    (:_id new-entity)
                                    new-entity)]
             {:entity body}))
  :processable? (fn [{:keys [request body]}]
                  (if-not (= :post (:request-method request))
                    [true {}]
                    (let [result (-> request
                                     :body
                                     (mc/find-one-as-map "user"
                                                         {:slug user-slug
                                                          :chart.slug (:slug body)}))]
                      (if-not (nil? result)
                        [false {:error "Duplicate entry."}]
                        [true {}]))))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :post (:request-method request))
                  [false {}]
                  (let [body (:body request)
                        error (validate acv/chart-validator body)]
                    (if-not (nil? error)
                      [true {:error error}]
                      (let [processed (assoc body :creator-slug (-> request
                                                                    :identity
                                                                    :slug))
                            saveable (process acm/chart->saveable processed)]
                        [false {:body saveable}]))))))
