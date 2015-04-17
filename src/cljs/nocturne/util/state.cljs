(ns cljs.nocturne.util.state
  (:require [cljs.nocturune.state :as state]))

(defn get-history
  []
  (:history @state/app-state))

(defn get-cookies
  []
  (:cookies @state/app-state))

(defn get-users
  []
  (:users @state/app-state))

(defn get-self
  []
  (:self @state/app-state))

(defn get-content
  []
  (:content @state/app-state))
