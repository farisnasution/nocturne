(ns clj.nocturne.app.user.route
  (:require [clj.nocturne.app.user.resource :as aur])
  (:use compojure.core
        [clj.nocturne.system.db :only [db-middleware]]))

(defroutes user-route
  (context "/api/v1/user" []
           (ANY "/" [] (-> aur/list-user-resource
                           (db-middleware :dev)))
           (ANY "/:user-slug"
                [user-slug]
                (-> (aur/single-user-resource user-slug)
                    (db-middleware :dev)))))

(defroutes user-util-route
  (context "/api/v2/user" []
           (ANY "/login" [] (-> aur/login-resource
                                (db-middleware :dev)))
           (ANY "/password" [] (-> aur/update-password-resource
                                   (db-middleware :dev)))))
