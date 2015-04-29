(ns cljs.nocturne.util.chart)

(defn translate
  [{:keys [pos-x pos-y]}]
  (let [x (if (number? pos-x) pos-x 0)
        y (if (number? pos-y) pos-y 0)]
    (str "translate(" x "," y ")")))
