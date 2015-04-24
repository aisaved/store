(ns centipair.admin.routes
  (:require [compojure.core :refer :all]
            [centipair.layout :as layout]
            [environ.core :refer [env]]
            [centipair.core.contrib.response :as response]))

(defn admin-dashboard []
  (layout/render
    "admin/dashboard.html" ))

(defn admin-home []
  (layout/render
   "admin/sbadmin.html"))


(defn admin-settings []
  (layout/render "admin/settings.html"))


(defn app-cache []
  (response/admin-appcache))


(defroutes admin-routes
  (GET "/admin" [] (admin-home))
  (GET "/admin/dashboard" [] (admin-dashboard))
  (GET "/admin/settings" [] (admin-settings))
  (GET "/ad.appcache" [] (app-cache))
  )

