(ns centipair.site.api
  (:use compojure.core
        centipair.site.settings
        centipair.site.page)
  (:require [liberator.core :refer [resource defresource]]
            ))


(defresource admin-api-site [&[source]]
  :available-media-types ["application/json"]
  :allowed-methods [:post :get :delete :put]
  :processable? (fn [context] (if (= (:request-method (:request context)) :get)
                                true
                                (validate-site (:params (:request context)))))
  :exists? (fn [context] (if (nil? source) true (site-exists?  source)))
  :handle-unprocessable-entity (fn [context] (:validation-result context))
  :post! (fn [context] (save-site (:params (:request context))))
  :handle-created (fn [context] (:created context))
  :handle-ok (fn [context] (if (nil? source) (get-all-sites) (get-site source))))


(defresource admin-api-page [&[source]]
  :available-media-types ["application/json"]
  :allowed-methods [:post :get :delete :put]
  :processable? (fn [context] (if (= (:request-method (:request context)) :get)
                                true
                                (if (= (:request-method (:request context)) :delete)
                                  true
                                  (validate-page (:params (:request context))))))
  :exists? (fn [context] (if (nil? source) true (page-exists?  source)))
  :handle-unprocessable-entity (fn [context] (:validation-result context))
  :post! (fn [context]
           (save-page (:params (:request context))))
  :handle-created (fn [context] (:created context))
  :delete! (fn [context]  (remove-page (:params (:request context))))
  :delete-enacted? false
  :handle-ok (fn [context] (if (nil? source) 
                             (get-all-pages (:params (:request context))) 
                             (get-page source))))


(defroutes admin-api-site-routes
  (ANY "/admin/api/page" [] (admin-api-page))
  (ANY "/admin/api/page/:id" [id] (admin-api-page id))
  (ANY "/admin/api/site" [] (admin-api-site))
  (ANY "/admin/api/site/:id" [id] (admin-api-site id)))
