(ns clj.nocturne.app.base.rule
  (:use [enigma.doc.validator.rule :only [defrule]]))

(defrule oid-only
  {:message (fn [_ value]
              (str "Value is not an instance of ObjectId. "
                   "Value: " value ". "
                   "Type: " (if-not (nil? value)
                              (type value)
                              "nil") "."))}
  [_ value]
  (instance? org.bson.types.ObjectId (type value)))
