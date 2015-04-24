(ns centipair.site.middleware
  (:require [centipair.site.models :refer [select-request-domain]]))

;;TODO: Implement cache here
(defn fetch-domain [request]
  (let [domain-name (:server-name request)
        bare-domain-name (clojure.string/replace domain-name #"www\." "")
        ]
    (select-request-domain domain-name bare-domain-name)))

(defn resolve-domain [handler]
  (fn [request]
     (let [site-domain (fetch-domain request)]
       (if (nil? site-domain)
         (handler request)
         (let [site-request (fetch-domain request)
               params (:params request)]
           (handler (assoc request :params 
                           (assoc params :site-id 
                                  (:site_settings_id site-domain)))))))))
