(ns clj.nocturne.system.settings
  (:require [buddy.core.nonce :as nonce]))

(def secret-key (nonce/random-bytes 32))

(def server-settings {:dev {:host "localhost"
                            :port 8080
                            :path "/"}})

(def db-settings {:dev {:host "localhost"
                        :port 27017
                        :db-name "default"}})
