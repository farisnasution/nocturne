(ns clj.nocturne.core
  (:require [io.clojure.liberator-transit]
            [clj.nocturne.system.server :as ss]
            [clj.nocturne.system.db :as sd])
  (:use [clj.nocturne.route :only [app-routes]]))

(defn start-app!
  [profile-name]
  (do
    (ss/start-server! app-routes profile-name)
    (sd/start-db! profile-name)))

(defn stop-app!
  [profile-name]
  (do
    (ss/stop-server! profile-name)
    (sd/stop-db! profile-name)))

;; (start-app! :dev)

;; (stop-app! :dev)

;; (require '[clj-http.client :as c])

;; (c/post "http://localhost:8080/api/v1/user"
;;         {:content-type :transit+json
;;          :accept "application/transit+json"
;;          :form-params {:name "farisnasution"
;;                        :password "mamposloe123"
;;                        :email "faris.nasution156@gmail.com"}})

;; ;; (c/get "http://localhost:8080/api/v1/user"
;; ;;         {:accept "application/transit+json"})

;; (def token "eyJhbGciOiJBMjU2S1ciLCJ0eXAiOiJKV1MiLCJlbmMiOiJBMTI4R0NNIn0.VrNXrZ-hPoVqWgCGOsMT9ifXEiyqtnU-.5EfimL3QrXTjl7fQ.tzcDzfZ4LT8g_gJGtW8nxCWxMw_hvQFfY87AE6wLA97fGRbedxEUkUpDljdSg8-8a7Bp1TEgtwA-R9UA9aUSuLrgS0ayxUaDJ8FRRPVg6Uws928rl4gO2g79n2fLDjeur9-FcvQNaJdppzSiXNHItOHDdGK3HVQn9R6B_e80z2uWzEzlLMuS5P1JTpsILF_mdxK8Lf7fgrDPwlJHBIr1ddXLx40FbXx543QxGcpjZHa-IZ7MYWLq38XDRWHHFpdzeXEoRwPY5iNqqJ_gS2Rwu4vRaezD.KLWhM84woZr8xLJqGYFrqw")

;; (c/post "http://localhost:8080/api/v2/user/login"
;;         {:content-type :transit+json
;;          :accept "application/transit+json"
;;          :form-params {:email "faris.nasution156@gmail.com"
;;                        :password "mamposloe123"}})

;; (c/put "http://localhost:8080/api/v2/user/password"
;;        {:content-type :transit+json
;;         :accept "application/transit+json"
;;         :headers {:authorization (str "Token " token)}
;;         :form-params {:old-password "mamposloe123"
;;                       :new-password "django123"}})

;; (c/get "http://localhost:8080/api/v1/user/farisnasution"
;;        {:accept "application/transit+json"
;;         :headers {:authorization (str "Token " token)}})

;; (c/put "http://localhost:8080/api/v1/user/farisnasution"
;;        {:accept "application/transit+json"
;;         :content-type :transit+json
;;         :headers {:authorization (str "Token " token)}
;;         :form-params {:name "angga"
;;                       :email "fewjhfew@fwef.com"}})

;; (c/delete "http://localhost:8080/api/v1/user/farisnasution"
;;           {:headers {:authorization (str "Token " token)}})

;; (c/get "http://localhost:8080/api/v1/user/farisnasution/data"
;;        {:accept "application/transit+json"
;;         :headers {:authorization (str "Token " token)}})

;; (c/post "http://localhost:8080/api/v1/user/farisnasution/data"
;;         {:accept "application/transit+json"
;;          :content-type :transit+json
;;          :headers {:authorization (str "Token " token)}
;;          :form-params {:name "mydata"
;;                        :url "http://localhost/api/v1/valve"
;;                        :description "dummy api"}})

;; (c/get "http://localhost:8080/api/v1/user/farisnasution/data/mydata"
;;        {:accept "application/transit+json"
;;         :headers {:authorization (str "Token " token)}})

;; (c/delete "http://localhost:8080/api/v1/user/farisnasution/data/mydata"
;;           {:accept "application/transit+json"
;;            :headers {:authorization (str "Token " token)}})

;; (c/put "http://localhost:8080/api/v1/user/farisnasution/data/mydata1"
;;        {:accept "application/transit+json"
;;         :content-type :transit+json
;;         :headers {:authorization (str "Token " token)}
;;         :form-params {:name "howdyho"
;;                       :url "http://howdyho.com"
;;                       :description "HOHO HAHA"}})
