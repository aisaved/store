(ns centipair.store.product
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [centipair.core.utilities.validators :as v]
            [centipair.core.components.input :as input]
            [centipair.core.ui :as ui]
            [centipair.admin.action :as action]
            [centipair.admin.images :as images]
            [centipair.admin.channels :refer [product-type-channel
                                              site-settings-id
                                              set-active-channel
                                              product-list-channel
                                              product-create-channel
                                              product-page-selector-channel]]
            [cljs.core.async :refer [put! chan <!]]
            ))


(def product-type-id {:id "product-type-id" :type "hidden"})
(def product-type-name (reagent/atom {:id "product-type-name" :type "text" :label "Name: " :validator v/required}))

(def product-type-attribute-name {:id "product-type-attribute-name" :type "text"})
(def product-type-attribute-type {:id "product-type-attribute-type" :type "text"})
(def product-type-attribute-label {:id "product-type-attribute-label" :type "text"})
(def product-type-attribute-sort-order {:id "product-type-attribute-sort-order" :type "text" :value-type "integer"})

(def attribute-name (reagent/atom {}))


(def product-type-attributes (reagent/atom []))


(defn delete-product-type []
  )

(defn product-type-action-bar [] (action/crud-action-bar
                                  {:create {:entity "product-type" :label "Create new product type" }
                                   :delete {:action delete-product-type :label "Delete selected product types"}}))

(def product-type-page (reagent/atom {:title "Product Type" :action-bar product-type-action-bar}))

(def new-product-type-page (reagent/atom {:title "New product Type"}))


(defn product-type []
  [:table]
  )

(defn new-product-type []
  [:form {:class "form-inline"}
   (input/plain-text product-type-name)
   ]
  )


(defn render-product-type []
  (ui/render-page product-type-page product-type))


(defn render-new-product-type-form []
  (ui/render-page new-product-type-page new-product-type))



(defn activate-product-type-page [site-id]
  
  )


(defn init-product-type-channel []
  (go (while true
         (activate-product-type-page (<! product-type-channel)))))


;; #################Product List####################


(defn product-list []
  [:div
   [:a {:href (str "#/site/" (:value @site-settings-id) "/product/create")
        :class "btn btn-primary"} "Add new product"]])


(defn render-product-list []
  (ui/render product-list "content"))


(defn fetch-product-list [site-id]
  (.log js/console (str "fetching products for " site-id)))


(defn activate-product-list [page-number]
  (set-active-channel product-list-channel)
  ;;(swap! page-data assoc :page (js/parseInt page-number))
  (if (not (nil? (:value @site-settings-id)))
    (put! product-list-channel (:value @site-settings-id)))
  (render-product-list))



(defn init-product-list-channel []
  (go (while true
         (fetch-product-list (<! product-list-channel)))))


;; ################# Product #######################
;; **** Product pages
;;General
;;Prices
;;Meta information
;;Images
;;Recurring Profile
;;Design
;;Gift Options
;;Inventory
;;Categories
;;Related Products
;;Up-sells
;;Cross sells
;;Custom options
;;Product attributes -> based on product type


(def product-page-state (reagent/atom {:id "product-page-selector" :value "general"}))



(defn product-page-selector []
  [:div {:id "product-page-selector-container"}
   [:h3 "Select an option"]
   [:select {:class "form-control"
             :value (:value @product-page-state)
             :on-change #(put! product-page-selector-channel (-> % .-target .-value))
             :id (:id @product-page-state) :key "product-page-selector"
             }
    [:option {:value "general" :key (str (:id @product-page-state) "-general")} "General"]
    [:option {:value "prices" :key (str (:id @product-page-state) "-prices")} "Prices"]
    [:option {:value "meta-info" :key (str (:id @product-page-state) "-meta-info")} "Meta Information"]
    [:option {:value "images" :key (str (:id @product-page-state) "-images")} "Images"]
    [:option {:value "recurring-profile" :key (str (:id @product-page-state) "-recurring-profile")} "Recurring Profile"]
    [:option {:value "design" :key (str (:id @product-page-state) "-design")} "Design"]
    [:option {:value "gift-options" :key (str (:id @product-page-state) "-gift-options")} "Gift Options"]
    [:option {:value "inventory" :key (str (:id @product-page-state) "-inventory")} "Inventory"]
    [:option {:value "categories" :key (str (:id @product-page-state) "-categories")} "Categories"]
    [:option {:value "related-products" :key (str (:id @product-page-state) "-related-products")} "Related Products"]
    [:option {:value "up-sells" :key (str (:id @product-page-state) "-up-sells")} "Up-sells"]
    [:option {:value "cross-sells" :key (str (:id @product-page-state) "-cross-sells")} "Cross-sells"]
    [:option {:value "custom-options" :key (str (:id @product-page-state) "-custom-options")} "Custom options"]
    [:option {:value "product-attributes" :key (str (:id @product-page-state) "-product-attributes")} "Product attributes"]]])



;;General options
(def product-title (reagent/atom {:id "product-title" :label "Product Title" :type "text"}))
(def product-sku (reagent/atom {:id "product-sku" :label "Product SKU" :type "text"}))
(def product-short-description (reagent/atom {:id "product-short-description" :label "Product short description" :type "textarea"}))
(def product-long-description-guide (reagent/atom {:id "product-long-description-guide" :label "Product long description supports markdown" :type "description"}))
(def product-long-description (reagent/atom {:id "product-long-description" :label "Product long description" :type "textarea"}))
(def product-active (reagent/atom {:id "product-active" :label "Product active" :type "checkbox" :description "Check this to make this product visible in the catalogue"}))
(def product-condition (atom {:id "product-condition" :type "select" :label "Product condition"
                              :options [{:label "Brand New" :value "new"}
                                        {:label "Refurbished" :value "refurbished"}
                                        {:label "Used - Like New" :value "used-like-new"}
                                        {:label "Used - Very Good" :value "used-very-good"}
                                        {:label "Used - Good" :value "used-good"}
                                        {:label "Used - Acceptable" :value "used-acceptable"}
                                        ]}))
 
(def product-featured (reagent/atom {:id "product-featured" :label "Featured Product" :type "checkbox"}))


(defn save-general-options []
  
  )
(def general-options-form (reagent/atom {:id "general-options-form" :title "General options" :type "form"}))
(def general-options-button (reagent/atom {:id "general-options-button" :type "button" :label "Save" :on-click save-general-options}))

(defn create-general-options-form []
  (input/form-aligned general-options-form [product-title
                                            product-sku
                                            product-short-description
                                            product-long-description-guide
                                            product-long-description
                                            product-active
                                            product-condition
                                            product-featured
                                            ] general-options-button))


;;Prices
(def product-cost-price (reagent/atom {:id "product-cost-price" :label "Cost Price" :type "text" :size 1}))
(def product-selling-price (reagent/atom {:id "product-selling-price" :label "Selling Price" :type "text" :size 1}))
(def product-offer-price (reagent/atom {:id "product-offer-price" :label "Offer Price" :type "text" :size 1}))
(def product-offer-price-start-date (reagent/atom {:id "product-offer-price-start-date" :label "Offer start date" :type "datepicker"}))
(def product-offer-price-end-date (reagent/atom {:id "product-offer-price-end-date" :label "Offer end date" :type "datepicker"}))

(defn save-price [])
(def product-price-button (reagent/atom {:id "product-price-button" :label "Save" :on-click save-price}))
(def price-options-form (reagent/atom {:id "price-options-form" :title "Price options" :type "form"}))

(defn create-price-options-form []
  (input/form-aligned price-options-form [product-cost-price
                                          product-selling-price
                                          product-offer-price
                                          product-offer-price-start-date
                                          product-offer-price-end-date
                                          ] product-price-button))

;;Meta information

(def product-url (reagent/atom {:id "product-url" :label "Product URL" :type "text"}))
(def product-meta-tags (reagent/atom {:id "prodct-meta-tags" :label "Product Meta Tags" :type "text"}))
(def product-meta-description (reagent/atom {:id "product-meta-description" :label "Product Meta Description" :type "textarea"}))

(defn save-meta-information []
  )

(def meta-information-form (reagent/atom {:id "meta-information-form" :label "Meta Information Form" :type "form"}))
(def meta-information-button (reagent/atom {:id "meta-information-button" :label "Save" :on-click save-meta-information}))

(defn create-meta-information-form []
  (input/form-aligned meta-information-form [product-url
                                             product-meta-tags
                                             product-meta-description]
                      meta-information-button))

;;Images
(def product-images (reagent/atom {:id "product-images" :form "product-images-form" :post "/admin/store/image"}))

(defn create-images-form []
  (images/image-file-component product-images))


;;Recurring Profile

(def product-recurring-profile-name     (reagent/atom {:id "product-recurring-profile-name" :type "text" :label "Name"}))
(def product-recurring-profile-active   (reagent/atom {:id "product-recurring-profile-active" :label "Active" :type "checkbox"}))
(def product-recurring-profile-duration (reagent/atom {:id "product-recurring-profile-duration" :type "text" :label "Duration"}))
(def product-recurring-profile-factor   (reagent/atom {:id "product-recurring-profile-factor" :type "select" :label "Factor"
                                                       :options [{:label "Daily" :value "daily"}
                                                                 {:label "Weekly" :value "weekly"}
                                                                 {:label "Monthly" :value "monthly"}
                                                                 {:label "Yearly" :value "yearly"}]}))


(defn save-recurring-profile [])
(def recurring-profile-button (reagent/atom {:id "recurring-profile-button" :on-click save-recurring-profile}))
(def recurring-profile-form (reagent/atom {:id "recurring-profile-form" :label "Recurring Profile Form" :type "form"}))

(defn create-recurring-profile-form []
  (input/form-aligned recurring-profile-form
                      [product-recurring-profile-name
                       product-recurring-profile-active
                       product-recurring-profile-duration
                       product-recurring-profile-factor]
                      recurring-profile-button))





(defn select-product-page []
  (case (:value @product-page-state)
     "general" (create-general-options-form)
     "prices" (create-price-options-form)
     "meta-info" (create-meta-information-form)
     "images" (create-images-form)
     "recurring-profile" (create-recurring-profile-form)
     (create-general-options-form)))

(defn product-page []
  [:div
   {:id "product-page"}
   [:h3 "Product"]
   (product-page-selector)
   (select-product-page)
   ])

(defn render-create-product []
  (ui/render product-page "content"))

(defn activate-create-product []
  (set-active-channel product-create-channel)
  (if (not (nil? (:value @site-settings-id)))
    (put! product-create-channel (:value @site-settings-id)))
  (render-create-product))


(defn process-product-page-selector [product-page-key]
  (swap! product-page-state assoc :value product-page-key))

(defn init-product-page-selector-channel []
  (go (while true
         (process-product-page-selector (<! product-page-selector-channel)))))

;; ################# Product #######################
