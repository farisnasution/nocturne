(ns clj.nocturne.app.data.resource
  (:require [clj.nocturne.app.data.validator :as adv]
            [clj.nocturne.app.data.mapper :as adm]
            [clj.nocturne.app.base.util :as abu]
            [clj.nocturne.app.base.resource :as abr]
            [monger.collection :as mc]
            [monger.query :as mq])
  (:use [liberator.core :only [defresource]]
        [enigma.doc.mapper :only [process]]
        [enigma.doc.validator.core :only [validate]]))

(defn -update-data
  [data-slug body]
  (fn [data]
    (if-not (= data-slug (:slug data))
      data
      (merge data body))))

(defresource single-data-resource
  [user-slug data-slug]
  (merge abr/single-resource
         (abr/ownership-auth-resource #{"user"}
                                      #{:put :delete}
                                      user-slug))
  :handle-ok (fn [{:keys [entity]}]
               (->> entity
                    :data
                    (filter #(= data-slug (:slug %)))
                    first))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :put (:request-method request))
                  [false {}]
                  (let [body (-> request
                                 :body
                                 (select-keys [:name :url :description]))
                        error (validate adv/data-validator body)]
                    (if (nil? error)
                      [false {:body body}]
                      [true {:error error}]))))
  :exists? (fn [{:keys [request body]}]
             (let [data-updater (-update-data data-slug body)
                   entity (-> request
                              :db
                              (mc/find-one-as-map "user"
                                                  {:slug user-slug
                                                   :data.slug data-slug}))]
               (if (nil? entity)
                 [false {:error "Entity not found."}]
                 [true {:entity (if (nil? body)
                                  entity
                                  (update-in entity
                                             [:data]
                                             (fn [ds]
                                               (map data-updater ds))))}])))
  :put! (fn [{:keys [request entity]}]
          (mc/update-by-id (:db request) "user" (:_id entity) entity))
  :delete! (fn [{:keys [request entity]}]
             (let [result (update-in entity
                                     [:data]
                                     (fn [ds]
                                       (filter (fn [d]
                                                 (not= data-slug (:slug d)))
                                               ds)))]
               (mc/update-by-id (:db request)
                                "user"
                                (:_id entity)
                                result))))

(defresource list-data-resource
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
                                       [:data])
                   :data))
  :post! (fn [{:keys [request body]}]
           (let [db-instance (:db request)
                 coll-name "user"
                 new-entity (update-in
                             (mc/find-one-as-map db-instance
                                                 coll-name
                                                 {:slug user-slug})
                             [:data]
                             conj
                             body)
                 _ (mc/update-by-id db-instance
                                    coll-name
                                    (:_id new-entity)
                                    new-entity)]
             {:entity body}))
  :processable? (fn [{:keys [request body]}]
                  (if-not (= :post (:request-method request))
                    [true {}]
                    (let [result (-> request
                                     :db
                                     (mc/find-one-as-map "user"
                                                         {:slug user-slug
                                                          :data.slug (:slug body)}))]
                      (if-not (nil? result)
                        [false {:error "Duplicate entry."}]
                        [true {}]))))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :post (:request-method request))
                  [false {}]
                  (let [body (:body request)
                        error (validate adv/data-validator body)]
                    (if-not (nil? error)
                      [true {:error error}]
                      (let [processed (assoc body :creator-slug (-> request
                                                                    :identity
                                                                    :slug))
                            saveable (process adm/data->saveable processed)]
                        [false {:body saveable}]))))))
