(ns clj.nocturne.system.state)

(defn create-path
  [settings]
  (let [path-fn (juxt :host :port)]
    (->> settings
         path-fn
         (clojure.string/join "-")
         keyword)))

(def state (atom {:server {}
                  :db {}}))
