(ns centipair.store.settings
  (:use centipair.store.models)
  (:require [validateur.validation :refer :all]))

(def store-settings (atom nil))


(defn load-store-settings []
  (reset! store-settings (select-store-settings)))


(defn get-store-settings [site-settings-id]
  (select-store-settings site-settings-id))


(defn install-store-settings []
  (init-store-settings))


(defn get-store
  "Gets store based on id"
  [id]
  (select-store-settings id))


(defn get-all-stores
  []
  "Selects all stores"
  (select-all-stores))


(defn store-exists?
  "Checks whether the store exists for given id"
  [id]
  (if (nil? id)
    true
    (if (not (nil? (get-store id)))
      true
      {:subdue true})))


(defn save-store-settings [params]
  (if (nil? (:store-settings-id params))
    {:created (insert-store-settings params)}
    (do 
      (update-store-settings params)
      {:created (get-store (:site-settings-id params))})))


(defn save-catalog-settings [params]
  (if (nil? (:store-settings-id params))
    {:created (insert-catalog-settings params)}
    (do 
      (update-catalog-settings params)
      {:created (get-store (:site-settings-id params))})))



(defn save-inventory-settings [params]
  (if (nil? (:store-settings-id params))
    {:created (insert-inventory-settings params)}
    (do 
      (update-inventory-settings params)
      {:created (get-store (:site-settings-id params))})))


(defn save-tax-settings [params]
  (if (nil? (:store-settings-id params))
    {:created (insert-tax-settings params)}
    (do 
      (update-tax-settings params)
      {:created (get-store (:site-settings-id params))})))



(def store-validator
  (validation-set
   (presence-of :store-name)))


(defn validate-items-per-page
  ""
  [value]
  (if (integer? value)
    (if (< value 100)
      true
      false)
    false))
  
(def integer-error-message "Please enter an integer")

(def catalog-validator
  (validation-set
   (validate-by :items-per-page 
                validate-items-per-page 
                :message "Please enter an integer less than 100")
   (validate-by :catalog-images-width
                integer?
                :message integer-error-message)
   (validate-by :catalog-images-height
                integer?
                :message integer-error-message)
   (validate-by :single-product-image-width
                integer?
                :message integer-error-message)
   (validate-by :single-product-image-height
                integer?
                :message integer-error-message)
   (validate-by :product-thumbnails-height
                integer?
                :message integer-error-message)
   (validate-by :product-thumbnails-width
                integer?
                :message integer-error-message)))

(def inventory-validator
  (validation-set
   (validate-by :hold-stock
                integer?
                :message integer-error-message
                )
   (validate-by :low-stock-threshold
                integer?
                :message integer-error-message
                )
   (validate-by :out-of-stock-threshold
                integer?
                :message integer-error-message)))



(defn validate-store-settings
  [params validator]
  (let [validation-result (validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))



(defn validate-store
  [params]
  (case (:group params)
    "store" (validate-store-settings params store-validator)
    "catalog" (validate-store-settings params catalog-validator)
    "inventory" (validate-store-settings params inventory-validator)
    "tax" true))


(defn save-store [params]
  (case (:group params)
  "store" (save-store-settings params)
  "catalog" (save-catalog-settings params)
  "inventory" (save-inventory-settings params)
  "tax" (save-tax-settings params)
  {:message "No op"}))
