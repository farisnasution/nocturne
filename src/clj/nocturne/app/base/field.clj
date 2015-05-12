(ns clj.nocturne.app.base.field
  (:require [enigma.doc.rule :as r]
            [clj.nocturne.app.base.rule :as abr])
  (:use [enigma.doc.validator.field :only [deffield]]
        enigma.doc.validator.core))

(defn- extract-required
  [rule value]
  (if-not (nil? value)
    (not value)
    (not (-> rule :settings :pass-nill?))))

(defmacro extract-settings
  [rule value ks]
  `(if-not (nil? ~value)
     ~value
     (-> ~rule :settings ~@ks)))

(deffield oid-field
  :required r/required
  :oid-only abr/oid-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))
