(ns centipair.store.api
   (:use compojure.core
         centipair.store.settings)
   (:require [liberator.core :refer [resource defresource]]
             [centipair.core.contrib.response :as response]
             [clojure.java.io :as io]))



(defresource admin-api-store [&[source]]
  :available-media-types ["application/json"]
  :allowed-methods [:post :get :delete :put]
  :processable? (fn [context] (if (= (:request-method (:request context)) :get)
                                true
                                (validate-store (:params (:request context)))))
  :exists? (fn [context] (if (nil? source) true (store-exists? source)))
  :handle-unprocessable-entity (fn [context] (:validation-result context))
  :post! (fn [context] (save-store (:params (:request context))))
  :handle-created (fn [context] (:created context))
  :handle-ok (fn [context] (if (nil? source)
                             (get-all-stores)
                             (if (:subdue context) ;;'resource not' found subdued for greater good
                               {}
                               (get-store source)))))

(defn upload-image [request]
  (println request)
  (let [filename (:filename (:image (:params request)))
        tempfile (:tempfile (:image (:params request)))]
     (io/copy tempfile (io/file "resources" "public" "uploads" filename))
     (response/json-response {:status "done"})))

(defroutes admin-api-store-routes
  (ANY "/admin/api/store" [] (admin-api-store))
  (ANY "/admin/api/store/:id" [id] (admin-api-store id))
  (POST "/admin/store/image" request (upload-image request)))
