(ns clj.nocturne.app.base.resource
  (:use [liberator.core :only [defresource]]
        [buddy.auth :only [authenticated?]]))

(defresource main-resource
  [index-file]
  :available-media-types ["text/html"]
  :allowed-methods [:get]
  :handle-ok (fn [_]
               (slurp (str "resources/public/" index-file))))

(def single-resource
  {:service-available? {:representation {:media-type "application/transit+json"}}
   :available-media-types ["application/transit+json"]
   :allowed-methods [:get :put :delete]
   :handle-malformed :error
   :handle-not-found :error
   :can-put-to-missing? false
   :respond-with-entity true
   :delete-enacted? true})

(def list-resource
  {:service-available? {:representation {:media-type "application/transit+json"}}
   :available-media-types ["application/transit+json"]
   :allowed-methods [:get :post]
   :handle-created :entity
   :new? true
   :post-redirect? false
   :handle-malformed :error
   :handle-unprocessable-entity :error})

(defn base-auth-resource
  [roles]
  {:authorized? (fn [{:keys [request]}]
                  (authenticated? request))
   :allowed? (fn [{:keys [request]}]
               (let [user-roles (-> request
                                    :identity
                                    :roles)]
                 (boolean (some roles user-roles))))})

(defn ownership-auth-resource
  [roles methods user-slug]
  (let [auth-resource (base-auth-resource roles)]
    (update-in auth-resource
               [:allowed?]
               (fn [f]
                 (fn [ctx]
                   (let [request (:request ctx)
                         role-result (f ctx)]
                     (when (true? role-result)
                       (if-not (boolean (methods (:request-method request)))
                         true
                         (= user-slug (-> request
                                          :identity
                                          :slug))))))))))
