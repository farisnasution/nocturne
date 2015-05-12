(ns clj.nocturne.app.user.mapper
  (:require [clj.nocturne.app.base.mapper :as abm]
            [buddy.hashers.bcrypt :as hs])
  (:use [enigma.doc.mapper :only [defmapper]]))

(defmapper user->saveable
  {:data true
   :chart true
   :showcase true
   :roles true}
  abm/root-saveable-mapper
  :password (fn [_ v]
              (hs/make-password v))
  :roles (fn [_ v] ["user"])
  :data (fn [_ v] [])
  :chart (fn [_ v] [])
  :showcase (fn [_ v] []))

(defmapper user->sendable
  abm/root-sendable-mapper)

(defmapper user->passwordable
  :old-password  (fn [_ v]
                   (hs/make-password v))
  :new-password (fn [_ v]
                  (hs/make-password v)))
