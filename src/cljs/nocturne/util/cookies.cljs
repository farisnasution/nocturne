(ns cljs.nocturne.util.cookies
  (:import [goog.net Cookies]))

(defn cookies
  []
  (Cookies. js/document))

(defn cookies-as-map
  [c]
  (zipmap (.getKeys c) (.getValues c)))

(defn cookies-enabled?
  [c]
  (.isEnabled c))

(defn get-cookies-value
  ([c k v]
   (.get c k v))
  ([c k]
   (get-cookies-value c k nil)))

(defn set-cookies-value!
  ([c k v]
   (.set c k v))
  ([c k v max-age]
   (.set c k v max-age))
  ([c k v max-age path]
   (.set c k v max-age path))
  ([c k v max-age path domain]
   (.set c k v max-age path domain)))

(defn remove-cookies-value!
  ([c k]
   (.remove c k))
  ([c k path]
   (.remove c k path))
  ([c k path domain]
   (.remove c k path domain)))
