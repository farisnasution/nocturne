(ns cljs.nocturne.core
  (:require [cljs.core.async :as async :refer [<! put! chan]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.nocturne.util.history :as uh]
            [cljs.nocturne.util.state :as us]
            [cljs.nocturne.util.route :as ur]
            [cljs.nocturne.util.path :as up]
            [cljs.nocturne.util.auth :as ua])
  (:use-macros [cljs.core.async.macros :only [go-loop]]))

(def history-channel (chan))

(uh/set-callback! (us/get-history) #(put! history-channel %))

(go-loop []
  (let [event (<! history-channel)
        token (.-token event)]
    (secretary/dispatch! token)))

(ur/dispatch! (up/current-location) "Login")
