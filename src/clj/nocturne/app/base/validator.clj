(ns clj.nocturne.app.base.validator
  (:require [enigma.doc.field :as df]
            [clj.nocturne.app.base.field :as abf])
  (:use [enigma.doc.validator :only [defvalidator]]))

(defvalidator root-validator
  :_id abf/oid-field
  :name df/string-field
  :slug df/slug-field
  :date-created df/date-field)

(defvalidator child-validator
  :name df/string-field
  :slug df/slug-field
  :creator-slug df/slug-field
  :date-created df/date-field)
