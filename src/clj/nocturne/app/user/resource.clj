(ns clj.nocturne.app.user.resource
  (:require [clj.nocturne.app.user.validator :as auv]
            [clj.nocturne.app.user.mapper :as aum]
            [clj.nocturne.app.base.util :as abu]
            [clj.nocturne.app.base.resource :as abr]
            [monger.collection :as mc]
            [monger.query :as mq]
            [buddy.sign.jwe :as jwe]
            [buddy.hashers.bcrypt :as hs]
            [clj-time.core :as time])
  (:use [liberator.core :only [defresource]]
        [enigma.doc.mapper :only [process]]
        [enigma.doc.validator.core :only [validate]]
        [clj.nocturne.system.settings :only [secret-key]]
        [buddy.auth :only [authenticated?]]
        monger.operators))

(defresource single-user-resource
  [user-slug]
  (merge abr/single-resource
         (abr/ownership-auth-resource #{"user"}
                                      #{:put :delete}
                                      user-slug))
  :handle-ok (fn [{:keys [entity]}]
               (process aum/user->sendable (dissoc entity :password)))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :put (:request-method request))
                  [false {}]
                  (let [body (-> request
                                 :body
                                 (select-keys [:name :email]))
                        error (validate auv/user-update-validator body)]
                    (if (nil? error)
                      [false {:body body}]
                      [true {:error error}]))))
  :exists? (fn [{:keys [request body]}]
             (let [entity (-> request
                              :db
                              (mc/find-one-as-map "user" {:slug user-slug}))]
               (if (nil? entity)
                 [false {:error "Entity not found."}]
                 [true {:entity (merge entity body)}])))
  :put! (fn [{:keys [request entity]}]
          (mc/update-by-id (:db request) "user" (:_id entity) entity))
  :delete! (fn [{:keys [request entity]}]
             (mc/remove-by-id (:db request)
                              "user"
                              (:_id entity))))

(def -user-default-pagination
  {:page 0
   :per-page 20
   :order 1})

(defresource list-user-resource
  abr/list-resource
  :authorized? (fn [{:keys [request]}]
                 (if (= :get (:request-method request))
                   (authenticated? request)
                   true))
  :allowed? (fn [{:keys [request]}]
              (if (= :get (:request-method request))
                (let [user-roles (-> request
                                     :identity
                                     :roles)]
                  (boolean (some #{"user"} user-roles)))
                true))
  :handle-ok (fn [{:keys [request]}]
               (let [[page per-page order] (abu/modify-pagination
                                            -user-default-pagination
                                            request)
                     entities (-> request
                                  :db
                                  (mq/with-collection "user"
                                    (mq/find (dissoc (:params request)
                                                     [:page :per-page :order]))
                                    (mq/sort {:_id order})
                                    (mq/paginate :page page :per-page per-page)))]
                 (map (fn [e]
                        (process aum/user->sendable (dissoc e :password)))
                      entities)))
  :post! (fn [{:keys [request body]}]
           (let [result (mc/insert-and-return (:db request) "user" body)]
             {:entity (process aum/user->sendable (dissoc result :password))}))
  :processable? (fn [{:keys [request body]}]
                  (if-not (= :post (:request-method request))
                    [true {}]
                    (let [result (-> request
                                     :db
                                     (mc/find-one-as-map "user"
                                                         {$or [{:slug (:slug body)}
                                                               {:email (:email body)}]}))]
                      (if-not (nil? result)
                        [false {:error "Duplicate entry."}]
                        [true {}]))))
  :malformed? (fn [{:keys [request]}]
                (if-not (= :post (:request-method request))
                  [false {}]
                  (let [body (:body request)
                        error (validate auv/user-save-validator body)]
                    (if (nil? error)
                      [false {:body (process aum/user->saveable body)}]
                      [true {:error error}])))))

(defresource login-resource
  :service-available? {:representation {:media-type "application/transit+json"}}
  :authorized? (fn [{:keys [request]}]
                 (not (authenticated? request)))
  :available-media-types ["application/transit+json"]
  :allowed-methods [:post]
  :handle-ok (fn [{:keys [entity]}]
               (let [claims (assoc (process aum/user->sendable entity)
                                   :exp
                                   (time/plus (time/now) (time/seconds 3600)))
                     token (jwe/encrypt claims
                                        secret-key
                                        {:alg :a256kw :enc :a128gcm})]
                 {:token token}))
  :handle-unprocessable-entity :error
  :handle-malformed :error
  :post-redirect? false
  :new? false
  :respond-with-entity? true
  :malformed? (fn [{:keys [request]}]
                (let [body (:body request)
                      error (validate auv/user-login-validator body)]
                  (if (nil? error)
                    [false {:body body}]
                    [true {:error error}])))
  :processable? (fn [{:keys [request body]}]
                  (let [user (-> request
                                 :db
                                 (mc/find-one-as-map "user" {:email (:email body)}))]
                    (if (nil? user)
                      [false {:error "Wrong combination of username and password."}]
                      (let [correct-password? (hs/check-password
                                               (:password body)
                                               (:password user))]
                        (if-not correct-password?
                          [false {:error "Wrong combination of username and password."}]
                          [true {:entity (dissoc user :password)}]))))))

(defresource update-password-resource
  (abr/base-auth-resource #{"user"})
  :service-available? {:representation {:media-type "application/transit+json"}}
  :available-media-types ["application/transit+json"]
  :allowed-methods [:put]
  :handle-unprocessable-entity :error
  :handle-malformed :error
  :new? false
  :respond-with-entity? false
  :put! (fn [{:keys [request entity body]}]
          (mc/update-by-id (:db request)
                           "user"
                           (:_id entity)
                           (assoc entity :password
                                         (hs/make-password
                                          (:new-password body)))))
  :malformed? (fn [{:keys [request]}]
                (let [body (:body request)
                      error (validate auv/user-password-validator body)]
                  (if (nil? error)
                    [false {:body body}]
                    [true {:error error}])))
  :processable? (fn [{:keys [request body]}]
                  (let [user (-> request
                                 :db
                                 (mc/find-one-as-map "user"
                                                     {:slug (-> request
                                                                :identity
                                                                :slug)}))
                        correct-password? (when-not (nil? user)
                                            (hs/check-password
                                             (:old-password body)
                                             (:password user)))]
                    (if (true? correct-password?)
                      [true {:entity user}]
                      [false {:error "Wrong password."}]))))
