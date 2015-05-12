(ns clj.nocturne.app.data.mapper
  (:use [enigma.doc.mapper :only [defmapper]]
        [clj.nocturne.app.base.mapper :only [child-saveable-mapper]]))

(defmapper data->saveable
  child-saveable-mapper)
