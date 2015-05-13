(ns clj.nocturne.core
  (:require [io.clojure.liberator-transit]
            [clj.nocturne.system.server :as ss]
            [clj.nocturne.system.db :as sd])
  (:use [clj.nocturne.route :only [app-routes]]))

(defn start-app!
  [profile-name]
  (do
    (ss/start-server! app-routes profile-name)
    (sd/start-db! profile-name)))

(defn stop-app!
  [profile-name]
  (do
    (ss/stop-server! profile-name)
    (sd/stop-db! profile-name)))
