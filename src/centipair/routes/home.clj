(ns centipair.routes.home
  (:require [centipair.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [centipair.core.contrib.response :as response]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))


(defn home-page []
  (layout/render
    "index.html" {:docs (-> "docs/docs.md" io/resource slurp)}))


(defn about-page []
  
  (layout/render "about.html"))


(defn test-page [request]
  (response/json-response {:test "success"}))


(defn csrf-token []
  (response/json-response {:token *anti-forgery-token*}))


(defn app-cache []
  (response/appcache))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/csrf" [] (csrf-token))
  (GET "/test" request (test-page request))
  (GET "/app.appcache" [] (app-cache)))
