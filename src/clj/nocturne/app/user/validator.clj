(ns clj.nocturne.app.user.validator
  (:require [enigma.doc.field :as df])
  (:use [enigma.doc.validator :only [defvalidator]]
        [clj.nocturne.app.base.validator :only [root-validator]]))

;; (defvalidator user-update-validator
;;   root-validator
;;   :password df/string-field
;;   :email df/email-field
;;   :roles df/vec-field
;;   :data df/vec-field
;;   :chart df/vec-field
;;   :showcase df/vec-field)

(defvalidator user-save-validator
  :name df/string-field
  :email df/email-field
  :password df/string-field)

(defvalidator user-update-validator
  :name df/string-field
  :email df/email-field)

(defvalidator user-login-validator
  :email df/email-field
  :password df/string-field)

(defvalidator user-password-validator
  :old-password df/string-field
  :new-password df/string-field)
