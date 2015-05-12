(ns clj.nocturne.app.base.util
  (:require [slugger.core :as slugger])
  (:import org.bson.types.ObjectId))

(defn ->oid
  ([identifier]
   (ObjectId. identity))
  ([]
   (ObjectId.)))

(defn ->slug
  [s]
  (slugger/->slug s))

(defn -process-page
  [default value]
  (if (and (integer? value)
           (or (pos? value)
               (zero? value)))
    value
    default))

(defn -process-per-page
  [default value]
  (-process-page default value))

(defn -process-order
  [default value]
  (if (or (= value 1)
          (= value -1))
    value
    default))

(defn modify-pagination
  [default request]
  (let [{:keys [page per-page order]} (:params request)
        {default-page :page
         default-per-page :per-page
         default-order :order} default
         new-page (-process-page default-page page)
         new-per-page (-process-per-page default-per-page per-page)
         new-order (-process-order default-order order)]
    [new-page new-per-page new-order]))
