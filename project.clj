(defproject nocturne "0.1.0"
  :description "Finals"
  :url "http://github.com/farisnasution/nocturne"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.omcljs/om "0.8.8"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [figwheel "0.2.6"]
                 [hiccup "1.0.5"]
                 [sablono "0.3.4"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.3.11"]
                 [org.clojars.fterrier/om-autocomplete "0.1.0"]
                 [hodgepodge "0.1.3"]
                 [enigma "0.1.6"]
                 [io.clojure/liberator-transit "0.3.0"]
                 [org.immutant/immutant "2.0.0"]
                 [com.novemberain/monger "2.1.0"]
                 [slugger "1.0.1"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-transit "0.1.3"]
                 [compojure "1.3.4"]
                 [liberator "0.12.2"]
                 [buddy "0.5.3"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.0.5"]
                             [lein-figwheel "0.2.6"]
                             [hiccup-watch "0.1.2"]
                             [lein-haml-sass "0.2.7-SNAPSHOT"]
                             [jonase/eastwood "0.2.1"]
                             [lein-ancient "0.6.7"]
                             [lein-kibit "0.1.2"]
                             [lein-bikeshed "0.2.0"]]
                   :dependencies [[clj-http "1.1.2"]]}}
  :figwheel {:http-server-root "public"
             :port 3449
             :css-dirs ["resources/public/css"]}
  :jvm-opts ["-Xmx1G"]
  :aliases {"dev" ["figwheel" "dev"]
            "hw" ["hiccup-watch"]
            "omni" ["do" ["au"] ["kibit"] ["eastwood"] ["bikeshed"]]
            "au" ["ancient" "upgrade" ":all"]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"
                                       "src/figwheel"]
                        :compiler {:output-to "resources/public/js/nocturne.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true
                                   :pretty-print true}}
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/nocturne_prod.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]}
  :hiccup-watch {:input-dir "src/hiccup/nocturne"
                 :output-dir "resources/public"}
  :sass {:src "src/sass/nocturne"
         :output-directory "resources/public/css"
         :output-extension "css"}
  :eastwood {:exclude-linters [:unlimited-use]
             :exclude-namespaces [:test-paths]})
