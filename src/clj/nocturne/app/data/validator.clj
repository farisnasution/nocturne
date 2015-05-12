(ns clj.nocturne.app.data.validator
  (:require [enigma.doc.field :as df])
  (:use [enigma.doc.validator :only [defvalidator]]
        [clj.nocturne.app.base.validator :only [child-validator]]))

(defvalidator data-validator
  :name df/string-field
  :url df/url-field
  :description df/string-field)
