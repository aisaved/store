(ns centipair.site.models
  (:use korma.core
        centipair.core.db.connection
        markdown.core
        centipair.core.utilities.pagination
        ))


(defentity site_settings)


(defn init-site-settings
  "Initializing site settings"
  []
  (insert site_settings (values 
                         {:mail_option "smtp"
                          :active true
                          :site_name "Centipair"
                          :site_domain_name "centipair.com"
                          :site_link "http://centipair.com"
                          :site_email "info@centipair.com"
                          :site_phone "123-456-789-0"
                          :mail_host ""
                          :mail_user ""
                          :mail_password ""
                          :mail_port 587
                          :mail_ssl false
                          :mail_api_url ""
                          :facebook_client_id ""
                          :template_folder ""})))


(defn create-site-settings 
  [params]
  (insert site_settings (values 
                         {
                          :active (:site-active params)
                          :site_name (:site-name params)
                          :site_domain_name (:site-domain-name params)
                          :site_link (:site-link params)
                          :site_email (:site-email params)
                          :site_phone (:site-phone params)
                          :template_folder (:template-folder params)
                          })))


(defn all-site-settings []
  (select site_settings (order :site_settings_id)))


(defn select-site-settings
  "selects site settings"
  [id]
  (let [site-settings-id (Integer. id)]
    (if (= site-settings-id 0)
      (first (all-site-settings))
      (first (select site_settings (where {:site_settings_id site-settings-id}))))))


(defn select-site-domain [domain-name]
  (first (select site_settings (where {:site_domain_name domain-name}))))


(defn update-site-settings
  "Updates site settings"
  [params]
  (update site_settings
          (set-fields {:active (:site-active params)
                       :site_name (:site-name params)
                       :site_domain_name (:site-domain-name params)
                       :site_link (:site-link params)
                       :site_email (:site-email params)
                       :site_phone (:site-phone params)
                       :template_folder (:template-folder params)})
          
          (where {:site_settings_id (:site-settings-id params)})))


(defn update-mail-settings
  "Updates site email settings"
  [params]
  (update site_settings
          (set-fields {:mail_option (:mail-option params)
                       :mail_host (:mail-host params)
                       :mail_user (:mail-user params)
                       :mail_password (:mail-password params)
                       :mail_port (:mail-port params)
                       :mail_ssl (:mail-ssl params)
                       :mail_api_url (:mail-api-url params)})
          (where {:site_settings_id (:site-settings-id params)})))

;;Pages
(defentity page)


(defn create-page
  [params]
    (insert page 
            (values {:page_title (:page-title params)
                     :page_content (:page-content params)
                     :page_content_html (md-to-html-string (:page-content params))
                     :page_meta_keywords (:page-meta-keywords params)
                     :page_meta_description (:page-meta-description params)
                     :page_url (:page-url params)
                     :page_template (:page-template params)
                     :page_active (:page-active params)
                     :site_settings_id (:site-settings-id params)})))


(defn update-page
  [params]
  (update page 
          (set-fields {:page_title (:page-title params)
                       :page_content (:page-content params)
                       :page_content_html (md-to-html-string (:page-content params))
                       :page_meta_keywords (:page-meta-keywords params)
                       :page_meta_description (:page-meta-description params)
                       :page_url (:page-url params)
                       :page_template (:page-template params)
                       :page_active (:page-active params)
                       :site_settings_id (:site-settings-id params)})
          (where {:page_id (:page-id params)})))


(defn select-page
  [page-id]
  (first (select page (where {:page_id (Integer. page-id)}))))



(defn select-site-pages [params]
  (let [offset-limit-params (offset-limit (:page params) (:per params))
        site-settings-id (Integer. (:site-settings-id params))
        total (count (select page (fields :page_id)
                             (where
                              {:site_settings_id site-settings-id})))]
    {:result (select page (fields :page_title :page_active :page_id :site_settings_id)
                     (where
                      {:site_settings_id (Integer. (:site-settings-id params))})
                     (offset (:offset offset-limit-params))
                     (limit (:limit offset-limit-params)))
     :total total
     :page (if (nil? (:page params)) 0 (Integer. (:page params)))
     :site_settings_id site-settings-id}))


(defn delete-page
  [page-id]
  (delete page (where {:page_id (Integer. page-id)})))


(defn select-site-page
  [site-settings-id page-url]
  (first (select page (where {:site_settings_id (Integer. site-settings-id)
                              :page_url page-url}))))


(defn select-request-domain
  [domain-name bare-domain-name]
  (first (select site_settings (where (or {:site_domain_name domain-name}
                                          {:site_domain_name bare-domain-name})))))

