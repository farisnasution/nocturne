(ns clj.nocturne.app.chart.mapper
  (:use [enigma.doc.mapper :only [defmapper]]
        [clj.nocturne.app.base.mapper :only [child-saveable-mapper]]))

(defmapper chart->saveable
  child-saveable-mapper)
