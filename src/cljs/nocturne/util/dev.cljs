(ns cljs.nocturne.util.dev)

(defn log
  [d]
  (.log js/console (clj->js d)))

(defn intercept
  [d]
  (do
    (log d)
    d))
