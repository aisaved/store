(ns centipair.middleware

  (:require [centipair.session :as session]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [selmer.middleware :refer [wrap-error-page]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.session-timeout :refer [wrap-idle-session-timeout]]
            [noir-exception.core :refer [wrap-internal-error]]
            [ring.middleware.session.memory :refer [memory-store]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [centipair.admin.access :refer [is-admin?]]
            [ring.middleware.file :refer [wrap-file]]
            ))


(defn on-error
  [request value]
  {:status 403
   :headers {"Content-Type" "text/html"}
   :body "Not authorized"})


(def rules
  [{:uri "/admin*"
    :handler is-admin?}])

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(defn development-middleware [handler]
  (if (env :dev)
    (-> handler
        wrap-error-page
        wrap-exceptions)
    handler))

(defn production-middleware [handler]
  (-> handler
      
      (wrap-restful-format :formats [:json :edn :yaml :yaml-in-html :transit-json :transit-msgpack]) 
      (wrap-access-rules {:rules rules :on-error on-error})
      (wrap-idle-session-timeout
        {:timeout (* 60 30)
         :timeout-response (redirect "/")})
      (wrap-defaults
        (-> site-defaults 
            (assoc-in [:session :store] (memory-store session/mem))
            (assoc-in [:static :resources] false)))
      ;;(wrap-file (-> (java.io.File. "static") .getAbsolutePath)) #production: static file
      (wrap-internal-error :log #(timbre/error %))))
