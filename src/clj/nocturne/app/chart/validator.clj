(ns clj.nocturne.app.chart.validator
  (:require [enigma.doc.field :as df])
  (:use [enigma.doc.validator :only [defvalidator]]
        [clj.nocturne.app.base.validator :only [child-validator]]))

(defvalidator chart-validator
  :name df/string-field
  :description df/string-field
  :data-slug df/string-field
  :chart-type df/string-field
  :dimension df/map-field
  :measurement df/vec-field)
