(ns clj.nocturne.system.server
  (:use [clj.nocturne.system.state :only [state create-path]]
        [clj.nocturne.system.settings :only [server-settings]]
        [enigma.util :only [dissoc-in]]
        immutant.web))

(defn start-server!
  [handler profile-name]
  (let [settings (get server-settings profile-name)
        path [:server (create-path settings)]]
    (swap! state (fn [current]
                   (let [server-instance (get-in current path)]
                     (if (nil? server-instance)
                       (assoc-in current path (run handler settings))
                       current))))))

(defn stop-server!
  [profile-name]
  (let [settings (get server-settings profile-name)
        path [:server (create-path settings)]]
    (swap! state (fn [current]
                   (let [server-instance (get-in current path)]
                    (if-not (nil? server-instance)
                      (do
                        (stop server-instance)
                        (dissoc-in current path))
                      current))))))
