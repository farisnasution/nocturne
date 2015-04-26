(ns cljs.nocturne.util.state
  (:require [cljs.nocturne.state :as state]))

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

(defn get-user
  [user-slug]
  (get-in @state/app-state [:users user-slug]))

(defn get-datas
  [user-slug]
  (get-in @state/app-state [:users user-slug :data]))

(defn get-charts
  [user-slug]
  (get-in @state/app-state [:users user-slug :chart]))

(defn get-showcases
  [user-slug]
  (get-in @state/app-state [:users user-slug :showcase]))

(defn get-data
  [user-slug data-slug]
  (get-in @state/app-state [:users user-slug :data data-slug]))

(defn get-chart
  [user-slug chart-slug]
  (get-in @state/app-state [:users user-slug :chart chart-slug]))

(defn get-showcase
  [user-slug showcase-slug]
  (get-in @state/app-state [:users user-slug :showcase showcase-slug]))
