(ns clj.nocturne.route
  (:require [compojure.route :as route])
  (:use compojure.core
        [clj.nocturne.app.user.route :only [user-route
                                            user-util-route]]
        [clj.nocturne.app.data.route :only [data-route]]
        [clj.nocturne.app.chart.route :only [chart-route]]
        [ring.middleware.defaults :only [wrap-defaults
                                         site-defaults
                                         api-defaults]]
        [clj.nocturne.app.base.resource :only [main-resource]]
        [clj.nocturne.system.settings :only [secret-key]]
        [ring.middleware.transit :only [wrap-transit-body]]
        [buddy.auth.backends.token :only [jwe-backend]]
        [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

(defn- ignore-trailing-slash
  [handler]
  (fn [request]
    (let [uri (:uri request)]
      (handler (assoc request :uri (if (and (not= "/" uri)
                                            (.endsWith uri "/"))
                                     (subs uri 0 (dec (count uri)))
                                     uri))))))


(defroutes -api-routes
  user-route
  user-util-route
  data-route
  chart-route)

(def api-routes (-> -api-routes
                    (wrap-defaults api-defaults)
                    (wrap-transit-body {:keywords? true})))

(defroutes -file-routes
  (route/resources "/")
  (rfn [] (main-resource "index.html")))

(def file-routes (-> -file-routes
                     (wrap-defaults site-defaults)))

(defroutes -app-routes
  api-routes
  file-routes)

(def auth-backend (jwe-backend {:secret secret-key
                                :options {:alg :a256kw :enc :a128gcm}}))

(def app-routes (-> -app-routes
                    (wrap-authorization auth-backend)
                    (wrap-authentication auth-backend)
                    ignore-trailing-slash))
