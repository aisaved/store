(ns centipair.site.forms
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [centipair.core.components.input :as input]
            [centipair.core.components.tabs :as tabs]
            [centipair.admin.action :as action]
            [centipair.core.utilities.validators :as v]
            [centipair.core.utilities.ajax :as ajax]
            [centipair.core.ui :as ui]
            [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [put! chan <!]]
            [centipair.admin.resources :refer [site-source]]
            [centipair.admin.channels :refer [site-settings-channel
                                              reload-site-settings-data
                                              site-settings-id
                                              set-active-channel
                                              new-site-channel]]))

(def site-active (atom {:id "site-active" :type "checkbox" :label "Activate site" :description "Checking this activates your website"}))
(def site-name (atom {:id "site-name" :label "Site name" :type "text" :validator v/required}))
(def site-domain-name (atom {:id "site-domain-name" :label "Domain name" :type "text" :validator v/required}))
(def site-link (atom {:id "site-link" :label "Site link" :type "text" :validator v/required}))
(def site-email (atom {:id "site-email" :label "Site email" :type "text" :validator v/required}))
(def site-phone (atom {:id "site-phone" :label "Site phone" :type "text" }))
(def template-folder (atom {:id "template-folder" :label "Template Folder" :type "text" }))
(def site-group (atom {:id "group" :value "site"}))

(def mail-option (atom {:id "mail-option" :label "Mail option" :type "radio"
                        :options [{:id "use-smtp" :label "Use SMTP" :name "mail-option-radio" :value "smtp"}
                                  {:id "use-api" :label "Use API" :name "mail-option-radio" :value "api"}]}))

(def mail-host (atom {:id "mail-host" :label "Mail server" :type "text" }))
(def mail-user (atom {:id "mail-user" :label "Mail username" :type "text" }))
(def mail-password (atom {:id "mail-password" :label "Mail password" :type "text"}))
(def mail-port (atom {:id "mail-port" :label "Mail Port" :type "text" :validator v/integer-required :value-type "integer"}))
(def mail-ssl (atom {:id "mail-ssl" :label "Mail SSL" :type "checkbox"}))
(def mail-api-subheading (atom {:id "mail-api-subheading" :label "Mail API" :type "subheading"}))
(def mail-api-url (atom {:id "mail-api-url" :label "Mail API URL" :type "text" }))
(def mail-group (atom {:id "group" :value "mail"}))



(def site-form (atom {:title "Site Settings" :id "site-form"}))


(defn save-site []
  (ajax/form-post
   site-form
   (site-source (:value @site-settings-id))
   [site-active
    site-name
    site-domain-name
    site-link
    site-email
    site-phone
    template-folder
    site-settings-id
    site-group]
   (fn [response]
     (ajax/data-saved-notifier response)
     (reload-site-settings-data))))

(def save-site-button (atom {:label "Save" :on-click save-site :id "save-site-button"} ))

(defn create-site-form []
  (input/form-aligned site-form [site-active
                                 site-name
                                 site-domain-name
                                 site-link
                                 site-email
                                 site-phone
                                 template-folder
                                 ] save-site-button))



(def mail-settings-form (atom {:id "mail-settings-form" :title "Mail Settings"}))

(defn save-mail-settings []
  (ajax/form-post
   mail-settings-form
   (site-source (:value @site-settings-id))
   [mail-option
    mail-host
    mail-user
    mail-password
    mail-port
    mail-ssl
    mail-api-url
    site-settings-id
    mail-group]
   ajax/data-saved-notifier))

(def save-mail-settings-button (atom {:id "save-mail-settings-button" :label "Save" :on-click save-mail-settings}))

(defn create-mail-settings-form []
  (input/form-aligned mail-settings-form [mail-option
                                          mail-host
                                          mail-user
                                          mail-password
                                          mail-port
                                          mail-ssl
                                          mail-api-subheading
                                          mail-api-url
                                          ] save-mail-settings-button))


(def site-settings (atom {:id "site-settings" :label "Site Settings" :content create-site-form :active true :url "site-settings" :key "site-settings"}))
(def mail-settings (atom {:id "mail-settings" :label "Mail Settings" :content create-mail-settings-form :active false :url "mail-settings" :key "mail-settings"}))


(defn site-settings-tabs []
  (tabs/render-tabs [site-settings
                     mail-settings]))


(defn delete-site []
  (.log js.console "deleting site"))

(defn site-action-bar
  []
  (action/crud-action-bar {:create {:entity "site"} :delete {:action delete-site}}))

(def site-page (atom {:title "Site" :action-bar site-action-bar :message ""}))

(defn render-site-settings-tabs
  [site-id]
  (do
    (ui/render-page site-page site-settings-tabs)
    (set-active-channel site-settings-channel)))


(defn map-site-settings [response]
  (input/update-value site-settings-id (:site_settings_id response))
  (input/update-check site-active (:active response))
  (input/update-value site-name (:site_name response))
  (input/update-value site-domain-name (:site_domain_name response))
  (input/update-value site-link (:site_link response))
  (input/update-value site-email (:site_email response))
  (input/update-value site-phone (:site_phone response))
  (input/update-value template-folder (:template_folder response))
  (input/update-value mail-option (:mail_option response))
  (input/update-value mail-host (:mail_host response))
  (input/update-value mail-user (:mail_user response))
  (input/update-value mail-password (:mail_password response))
  (input/update-value mail-port (:mail_port response))
  (input/update-check mail-ssl (:mail_ssl response))
  (input/update-value mail-api-url (:mail_api_url response)))



(defn fetch-site-settings [site-id]
  (ajax/get-json (site-source site-id)
                 {}
                 (fn [response]
                   (map-site-settings response)
                   )))

(defn init-site-settings-channel []
  (go (while true
         (fetch-site-settings (<! site-settings-channel)))))


(def new-site-form (atom {:title "Create New Site" :id "site-form"}))
(def new-site-active (atom {:id "site-active" :label "Active" :type "checkbox" }))
(def new-site-name (atom {:id "site-name" :label "Site name" :type "text" :validator v/required}))
(def new-site-domain-name (atom {:id "site-domain-name" :label "Domain name" :type "text" :validator v/required}))
(def new-site-link (atom {:id "site-link" :label "Site link" :type "text" :validator v/required}))
(def new-site-email (atom {:id "site-email" :label "Site email" :type "text" :validator v/required}))
(def new-site-phone (atom {:id "site-phone" :label "Site phone" :type "text" }))
(def new-template-folder (atom {:id "template-folder" :label "Template Folder" :type "text" }))


(defn save-new-site []
  (ajax/form-post
   new-site-form
   (site-source)

   [new-site-active
    new-site-name
    new-site-domain-name
    new-site-link
    new-site-email
    new-site-phone
    new-template-folder
    site-group]
   (fn [response]
     (ajax/data-saved-notifier response)
     (reload-site-settings-data))))


(def save-new-site-button (atom {:label "Save" :on-click save-new-site :id "save-site-button"} ))
(defn create-new-site-form
  []
  (input/form-aligned new-site-form [new-site-active
                                 new-site-name
                                 new-site-domain-name
                                 new-site-link
                                 new-site-email
                                 new-site-phone
                                 new-template-folder
                                 ] save-new-site-button))

(defn reset-new-site-form
  []
  (input/reset-inputs [new-site-name
                       new-site-domain-name
                       new-site-link
                       new-site-email
                       new-site-phone
                       new-template-folder]))




(defn cancel-action-bar
  []
  (action/cancel-action-bar site-settings-id "site" reset-new-site-form))


(def new-site-page (atom {:title "New site" :message "" :action-bar cancel-action-bar}))

(defn render-new-site-form []
  (do
    (ui/render-page new-site-page create-new-site-form)
    (set-active-channel site-settings-channel)))




;;(defn init-new-site-channel [] (go (while true (fetch-site-settings (<! site-settings-channel)))))

