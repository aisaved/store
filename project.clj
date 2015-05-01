(defproject centipair "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.4.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.66"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.3"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.5.0"]
                 [noir-exception "0.2.5"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [org.clojure/tools.reader "0.9.2"]
                 [org.clojure/clojurescript "0.0-3211" :scope "provided"]
                 [reagent "0.5.0"]
                 ;;[reagent-forms "0.4.6"]
                 ;;[reagent-utils "0.1.4"]
                 [secretary "1.2.3"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-ajax "0.3.11"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.1"]
                 [org.immutant/immutant "2.0.0"]
                 [crypto-random "1.2.0"]
                 [danlentz/clj-uuid "0.1.5"]
                 [liberator "0.12.2"]
                 [com.novemberain/validateur "2.4.2"]
                 [cheshire "5.4.0"]
                 [korma "0.4.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [clj-time "0.9.0"]
                 [buddy "0.5.2"]
                 [clojurewerkz/cassaforte "2.0.1"]
                 [org.xerial.snappy/snappy-java "1.1.1.7"]
                 [com.draines/postal "1.11.3"]]

  :min-lein-version "2.0.0"
  :uberjar-name "centipair.jar"
  :repl-options {:init-ns centipair.handler
                 :init (centipair.repl/start-server)}
  :jvm-opts ["-server"]
  :main centipair.main
  :plugins [[lein-ring "0.9.1"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [lein-cljsbuild "1.0.5"]]
  

  :ring {:handler centipair.handler/app
         :init    centipair.handler/init
         :destroy centipair.handler/destroy
         :uberwar-name "centipair.war"}
  
  
  :clean-targets ^{:protect false} ["resources/public/cljs"
                                    "resources/public/cljs-admin"]
  
  
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src-cljs"]
     :compiler
     {:output-dir "resources/public/cljs/out"
      :externs ["react/externs/react.js"]
      :optimizations :none
      :output-to "resources/public/cljs/main.js"
      :source-map "resources/public/cljs/out.js.map"
      :pretty-print true}}
    :app-admin
    {:source-paths ["src-cljs-admin"]
     :compiler
     {:output-dir "resources/public/cljs-admin/out"
      :externs ["react/externs/react.js"]
      :optimizations :none
      :output-to "resources/public/cljs-admin/admin-main.js"
      :source-map "resources/public/cljs-admin/out.js.map"
      :pretty-print true}}
    }}
  
  :immutant {:war 
             {:context-path "/"}}
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :hooks [leiningen.cljsbuild]
              :cljsbuild
              {:jar true
               :builds
               {:app
                {:source-paths ["env/prod/cljs"]
                 :compiler {:optimizations :advanced :pretty-print false}}
                :app-admin
                {:source-paths ["env/prod/cljs-admin"]
                 :compiler {:optimizations :advanced :pretty-print false}}}} 
             
             :aot :all}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        [leiningen "2.5.1"]
                        [figwheel "0.2.8"]
                        [weasel "0.6.0"]
                        [com.cemerick/piggieback "0.2.1"]]
         :source-paths ["env/dev/clj"]
         
         :plugins [[lein-figwheel "0.2.3-SNAPSHOT"]]
         
         :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}
                              :app-admin {:source-paths ["env/dev/cljs-admin"]}}} 
         
         
         :figwheel
         {:http-server-root "public"
          :server-port 3449
          :css-dirs ["resources/public/css"]
          :ring-handler centipair.handler/app}
         
         
         :repl-options {:init-ns centipair.repl}
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}}
  )
