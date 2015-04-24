(ns centipair.site.settings
  (:use centipair.site.models)
  (:require [validateur.validation :refer :all]
            ))


(def site-settings (atom nil))


(defn install-site-settings []
  (init-site-settings))


(defn load-site-settings []
  (let [settings-data (all-site-settings)]
    (do 
      (reset! site-settings nil)
      (doseq [each settings-data]
        (swap! site-settings assoc 
               (keyword (:site_domain_name each))
               each)))))


(defn get-site [id]
  (select-site-settings id))

(defn get-all-sites []
  (all-site-settings))


(defn site-exists? [id]
  (if (nil? id)
    true
    (not (nil? (get-site id)))))


(def site-settings-validator
  (validation-set
   (presence-of :site-domain-name :message "Your site needs a domain name")
   (presence-of :site-link :message "Site link required Example :http://www.mysite.com")
   (presence-of :site-email :message "Please provide an email address to use as Site's Email")))


(defn unique-domain-name-valid [params]
  (let [site-settings-domain (select-site-domain (:site-domain-name params))]
    (if (= (:site_settings_id site-settings-domain) (:site-settings-id params))
      true
      (if (= (:site_domain_name site-settings-domain) (:site-domain-name params))
        [false {:validation-result {:errors {:site-domain-name ["Another site have the same domain name"]}}}]
        true))))
  



(def mail-settings-validator
  (validation-set
   (presence-of :site-settings-id :message "Related site not found")))


(defn validate-mail-settings
  "Validating mail settings"
  [params]
  (let [validation-result (mail-settings-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))


(defn update-reload-mail-settings
  "uploads site settings and reload cached settings"
  [params]
  (do
    (update-mail-settings params)
    (load-site-settings)))
 

(defn validate-site-settings
  [params]
  (let [validation-result (site-settings-validator params)]
    (if (valid? validation-result)
      (unique-domain-name-valid params)
      [false {:validation-result {:errors validation-result}}])))


(defn validate-site
  "Validates site settings"
  [params]
  (case (:group params)
    "site" (validate-site-settings params)
    "mail" (validate-mail-settings params)))


(defn save-mail-settings
  "Updates mail settings"
  [params]
  (let [updated-mail-settings (update-mail-settings params)]
    (load-site-settings)
    {:created {:site-settings-id (:site-settings-id params)}}))


(defn save-site-settings
  "Saves/Updates site settings"
  [params]
  (if (nil? (:site-settings-id params))
    (let [new-site (create-site-settings params)]
      (load-site-settings)
      {:created {:site-settings-id (:site_settings_id new-site)}})
    (let [updated-site (update-site-settings params)]
      (load-site-settings)
      {:created {:site-settings-id (:site-settings-id params)}})))


(defn save-site
  "Saves a new site"
  [params]
  (case (:group params)
    "site" (save-site-settings params)
    "mail" (save-mail-settings params)
    {:message "No op"}))
