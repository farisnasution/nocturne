(ns cljs.nocturne.util.route
  (:require [om.core :as om :include-macros true]
            [cljs.nocturne.util.history :as uh]
            [cljs.nocturne.util.state :as us]
            [cljs.nocturne.util.auth :as ua]
            [cljs.nocturne.util.query :as uq])
  (:use [cljs.nocturne.state :only [root-id
                                    app-state]]
        [cljs.nocturne.login :only [login-page]]))

(defn dispatch!
  [destination & [title]]
  (if (ua/authenticated?)
    (if (string? title)
      (uh/set-token! (us/get-history) destination title)
      (uh/set-token! (us/get-history) destination))
    (om/root login-page app-state {:target (uq/by-id root-id)})))
