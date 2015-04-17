(ns cljs.nocturne.state
  (:require [cljs.nocturne.util.history :as uh]
            [cljs.nocturne.util.cookies :as uc]))

(def root-id "main")

(defonce app-state (atom {:history (uh/start-history)
                          :cookies (uc/cookies)
                          :users {}
                          :self {}
                          :content [:root]}))
