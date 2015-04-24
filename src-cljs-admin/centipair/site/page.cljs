(ns centipair.site.page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [centipair.core.components.input :as input]
            [centipair.core.components.tabs :as tabs]
            [centipair.core.utilities.validators :as v]
            [centipair.core.utilities.ajax :as ajax]
            [centipair.core.ui :as ui]
            [centipair.admin.url :refer [entity-url]]
            [centipair.admin.resources :refer [page-source]]
            [reagent.core :as reagent :refer [atom]]
            [centipair.admin.channels :refer [site-settings-id
                                              page-list-channel
                                              set-active-channel]]
            [cljs.core.async :refer [put! <!]]
            [centipair.core.components.table :refer [data-table generate-table-rows per-page]]))


(defn page-headers
  []
  [:tr
   [:th "Title"]
   [:th "Active"]
   [:th "Action"]
   ])

(defn delete-page [id]
  (ajax/delete
   (page-source id)
   (fn [response]
     (put! page-list-channel (:value @site-settings-id)))))


(def page-data (atom {:page 0
                      :id "admin-pages-table"
                      :url "pages"
                      :total 0
                      :rows [:tr [:td "Loading"]]
                      :headers (page-headers)
                      :create {:entity "page"} 
                      :delete {:action (fn [] (.log js/console "delete"))}
                      :site-settings-id nil
                      :id-field "page_id"
                      }))

(defn page-row [row-data]
  [:tr {:key (str "table-row-" ((keyword (:id-field @page-data)) row-data))}
   [:td {:key (str "table-column-1-" ((keyword (:id-field @page-data)) row-data))} (:page_title row-data)]
   [:td {:key (str "table-column-2-" ((keyword (:id-field @page-data)) row-data))} (str (:page_active row-data))]
   [:td {:key (str "table-column-3-" ((keyword (:id-field @page-data)) row-data))}
    [:a {:href (str "#/site/" (:site_settings_id row-data) "/page/edit/" (:page_id row-data))
         :key (str "row-edit-link-" ((keyword (:id-field @page-data)) row-data))} "Edit "]
    [:a {:href "javascript:void(0)" :on-click (partial delete-page (:page_id row-data))
        :key (str "row-delete-link-" ((keyword (:id-field @page-data)) row-data)) } " Delete"]]])

(defn create-page-data-list []
  (data-table page-data))


(defn load-site-pages [site-id]
  (swap! page-data assoc :site-settings-id site-id)
  (ajax/get-json 
   (page-source)
   {:site-settings-id site-id
    :page (:page @page-data)
    :per per-page}
   (fn [response]
     (generate-table-rows response page-data page-row))))


(defn render-page-list []
  (ui/render create-page-data-list "content"))



(def page-id (atom {:id "page-id" :value nil :type "hidden" }))
(def page-title (atom {:id "page-title" :type "text" :label "Page Title" :validator v/required}))
(def page-content (atom {:id "page-content" :type "markdown" :label "Page Content"}))

(def page-seo-subheading (atom {:id "page-seo-subheading" :label "Search Engine Optimization" :type "subheading"}))
(def page-url (atom {:id "page-url" :type "text" :label "Page URL" :validator v/required}))
(def page-meta-keywords (atom {:id "page-meta-keywords" :type "text" :label "Page Meta Keywords"}))
(def page-meta-description (atom {:id "page-meta-description" :type "textarea" :label "Page Meta Description"}))
(def page-active (atom {:id "page-active" :type "checkbox" :label "Active" :description "Publishes this page"}))
(def page-template (atom {:id "page-template" :label "Template" :validator v/required :default "page.html" :type "text"}))

(def page-form (atom {:id "page-form" :title "Page Editor"}))


(defn save-page []
  (ajax/form-post 
   page-form
   (page-source (:value @page-id))
   [page-title
    page-content
    page-url
    page-meta-keywords
    page-meta-description
    page-id
    site-settings-id
    page-active
    page-template]
   (fn [response]
     (ajax/data-saved-notifier response)
     ;;(swap! page-id assoc :value (:page-id response))
     (entity-url "/page/edit" (:page_id response)))))


(def save-page-button (atom {:label "Save" :on-click save-page :id "save-page-button"} ))

(defn create-page-form []
  (input/form-plain page-form [page-title
                               page-content
                               page-seo-subheading
                               page-url
                               page-meta-keywords
                               page-meta-description
                               page-active
                               page-template
                               ] save-page-button))



(defn map-page-form [response]
  (do
    (input/update-check page-active (:page_active response))
    (input/update-value page-id (:page_id response))
    (input/update-value page-title (:page_title response))
    (input/update-value page-content (:page_content response))
    (input/update-value page-url (:page_url response))
    (input/update-value page-meta-keywords (:page_meta_keywords response))
    (input/update-value page-meta-description (:page_meta_description response))
    (input/update-value page-template (:page_template response))
    ))


(defn reset-page-form []
  (input/reset-inputs [page-id
                       page-title
                       page-content
                       page-url
                       page-meta-keywords
                       page-meta-description
                       page-template]))

(defn render-crud-form []
  (reset-page-form)
  (ui/render create-page-form "content"))


(defn edit-page [id]
  (render-crud-form)
  (ajax/get-json
   (page-source id)
   nil
   (fn [response]
     (map-page-form response))))

(defn new-page []
  (render-crud-form))

(defn markdown-html-value []
  (.log js/console (:html @page-content)))



(defn init-page-list-channel []
  (go (while true
         (load-site-pages (<! page-list-channel)))))



(defn activate-page-list [page-number]
  (set-active-channel page-list-channel)
  (swap! page-data assoc :page (js/parseInt page-number))
  (if (not (nil? (:value @site-settings-id)))
    (put! page-list-channel (:value @site-settings-id)))
  (render-page-list))
