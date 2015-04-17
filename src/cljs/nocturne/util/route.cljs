(ns cljs.nocturne.util.route
  (:require [cljs.nocturne.util.history :as uh]
            [cljs.nocturne.util.state :as us]))

(defn dispatch!
  [destination]
  (uh/set-token! (us/get-history) destination))
