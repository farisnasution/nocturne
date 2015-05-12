(ns clj.nocturne.system.db
  (:require [monger.core :as mg])
  (:use [clj.nocturne.system.state :only [state create-path]]
        [clj.nocturne.system.settings :only [db-settings]]
        [enigma.util :only [dissoc-in]]))

(defn db-middleware
  [handler profile-name]
  (fn [request]
    (let [settings (get db-settings profile-name)
          path [:db (create-path settings)]
          new-request (assoc request :db (:instance (get-in @state path)))]
      (handler new-request))))

(defn start-db!
  [profile-name]
  (let [settings (get db-settings profile-name)
        path [:db (create-path settings)]]
    (swap! state (fn [current]
                   (let [db-entity (get-in current path)]
                     (if (nil? db-entity)
                       (update-in current
                                  path
                                  (fn [c s]
                                    (let [conn (mg/connect {:host (:host s)
                                                            :port (:port s)})]
                                      {:connection conn
                                       :instance (mg/get-db conn (:db-name s))}))
                                  settings)
                       current))))))

(defn stop-db!
  [profile-name]
  (let [settings (get db-settings profile-name)
        path [:db (create-path settings)]]
    (swap! state (fn [current]
                   (let [db-entity (get-in current path)]
                     (if-not (nil? db-entity)
                       (do
                         (mg/disconnect (:connection db-entity))
                         (dissoc-in current path))
                       current))))))
