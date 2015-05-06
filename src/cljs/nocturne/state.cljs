(ns cljs.nocturne.state
  (:require [cljs.nocturne.util.history :as uh]
            [hodgepodge.core :as hp]))

(def root-id "main")

(defonce app-state (atom {:history (uh/start-history)
                          :users {}
                          :self nil
                          :content [:root]}))
