(ns cljs.nocturne.util.data)

(defn ascend-by-keys
  ([ks comp coll]
   (sort (fn [a b]
           (let [val-a (get-in a ks)
                 val-b (get-in b ks)]
             (comp val-a val-b))) coll))
  ([ks coll]
   (ascend-by-keys ks compare coll)))

(defn descend-by-keys
  ([ks comp coll]
   (sort (fn [a b]
           (let [val-a (get-in a ks)
                 val-b (get-in b ks)]
             (comp val-b val-a))) coll))
  ([ks coll]
   (ascend-by-keys ks compare coll)))
