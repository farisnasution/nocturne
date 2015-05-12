(ns clj.nocturne.app.base.mapper
  (:require [enigma.util :as u]
            [clj.nocturne.app.base.util :as abu])
  (:use [enigma.doc.mapper :only [defmapper]]
        slugger.core))

(defmapper root-saveable-mapper
  {:_id true
   :slug true
   :date-created true}
  :_id (fn [_ v]
         (abu/->oid))
  :slug (fn [data v]
          (abu/->slug (:name data)))
  :date-created (fn [_ v]
                  (u/now)))

(defmapper root-sendable-mapper
  :_id (fn [_ v]
         (str v)))

(defmapper child-saveable-mapper
  {:slug true
   :date-created true}
  :slug (fn [data v]
          (abu/->slug (:name data)))
  :date-created (fn [_ v]
                  (u/now)))
