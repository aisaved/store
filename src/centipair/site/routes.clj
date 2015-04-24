(ns centipair.site.routes
  (:require [compojure.core :refer :all]
            [centipair.layout :as layout]
            [centipair.site.page :refer [fetch-site-page]]))


(defn site-page 
  [request]
  (let [site-page (fetch-site-page (:params request))]
    (layout/render "page.html" site-page)
    ))

(defroutes site-routes
  (GET "/page/:url" request (site-page request)))
