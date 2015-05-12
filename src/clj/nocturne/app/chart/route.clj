(ns clj.nocturne.app.chart.route
  (:require [clj.nocturne.app.chart.resource :as acr])
  (:use compojure.core
        [clj.nocturne.system.db :only [db-middleware]]))

(defroutes chart-route
  (context "/api/v1/user/:user-slug/chart" [user-slug]
           (ANY "/" [] (-> (acr/list-chart-resource user-slug)
                           (db-middleware :dev)))
           (ANY "/:chart-slug"
                [chart-slug]
                (-> (acr/list-chart-resource user-slug chart-slug)
                    (db-middleware :dev)))))
