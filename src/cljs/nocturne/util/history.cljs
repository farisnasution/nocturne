(ns cljs.nocturne.util.history
  (:require [goog.history.Html5History :as history5]
            [goog.history.EventType :as history-event]
            [cljs.nocturne.util.event :as ue])
  (:import [goog History]
           [goog.history Html5History]))

(defn start-history
  ([use-fragment?]
   (let [h (if (history5/isSupported)
             (Html5History.)
             (History.))
         _ (.setUseFragment h use-fragment?)
         _ (.setPathPrefix h "")
         _ (.setEnabled h true)
         _ (ue/unlisten (.-window_ h)
                        (:POPSTATE ue/event-type)
                        (.-onHistoryEvent_ h)
                        false
                        h)]
     h))
  ([]
   (start-history false)))

(defn set-token!
  ([history token opt-title]
   (.setToken history token opt-title))
  ([history token]
   (.setToken history token)))

(defn replace-token!
  ([history token opt-title]
   (.replaceToken history token opt-title))
  ([history token]
   (.replaceToken history token)))

(defn set-callback!
  [history callback-fn]
  (ue/listen history
             history-event/NAVIGATE
             callback-fn))
