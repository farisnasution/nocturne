(ns cljs.nocturne.util.path
  (:import [goog.uri utils]))

(defn current-location
  []
  (-> js/window .-location .-href))

(defn get-uri-domain
  [uri]
  (.getDomain utils uri))

(defn get-uri-host
  [uri]
  (.getHost utils uri))

(defn get-uri-fragment
  [uri]
  (.getFragment utils uri))

(defn get-uri-fragment-encoded
  [uri]
  (.getFragment utils uri))

(defn get-param-value
  [uri k]
  (.getParamValue utils uri k))

(defn get-param-values
  [uri k]
  (.getParamValues utils uri k))

(defn get-path
  [uri]
  (.getPath utils uri))

(defn get-path-and-after
  [uri]
  (.getPathAndAfter utils uri))

(defn get-path-encoded
  [uri]
  (.getPathEncoded utils uri))

(defn get-port
  [uri]
  (.getPort utils uri))

(defn get-query-data
  [uri]
  (.getQueryData utils uri))

(defn has-param
  [uri k]
  (.hasParam utils uri k))

(defn remove-fragment
  [uri]
  (.removeFragment utils uri))

(defn remove-param
  [uri k]
  (.removeParam uri k))

(defn set-param
  [uri k v]
  (.setParam uri k v))
