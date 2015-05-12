(ns clj.nocturne.app.data.route
  (:require [clj.nocturne.app.data.resource :as adr])
  (:use compojure.core
        [clj.nocturne.system.db :only [db-middleware]]))

(defroutes data-route
  (context "/api/v1/user/:user-slug/data" [user-slug]
           (ANY "/" [] (-> (adr/list-data-resource user-slug)
                           (db-middleware :dev)))
           (ANY "/:data-slug"
                [data-slug]
                (-> (adr/single-data-resource user-slug data-slug)
                    (db-middleware :dev)))))
