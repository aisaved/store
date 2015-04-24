(ns centipair.admin.channels
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [put! chan <!]]
            [centipair.core.utilities.ajax :as ajax]
            [centipair.core.ui :as ui]
            [centipair.core.utilities.dom :as gdom]
            [centipair.admin.resources :refer [site-source
                                               store-source]]))




(def site-settings-data (reagent/atom {:id "site-settings-selector" :options []}))
(def site-settings-id (reagent/atom {:id "site-settings-id" :type "hidden" :value nil :value-type "integer"}))
;;channels
(def dashboard-channel (chan))
(def site-settings-channel (chan))
(def new-site-channel (chan))
(def store-settings-channel (chan))
(def page-list-channel (chan))
(def page-channel (chan))
(def site-selector-channel (chan))
(def active-channel (atom nil))
(def admin-menu-channel (chan))
(def user-manager-form-channel (chan))
(def user-manager-list-channel (chan))

(def product-type-channel (chan))
(def product-create-channel (chan))
(def product-edit-channel (chan))

(def product-list-channel (chan))
(def product-page-selector-channel (chan))

(def product-image-upload-channel (chan))


;; Active page format:
;; {:url "/something/:site-id/something-else" :channel current-page-channel}
;; :site-id will be replaced with actual id

(def active-page (atom nil))


(defn activate-channel [site-id]
  (let [active-channel (:channel @active-page)]
    (if (not (nil? active-channel))
      (if (= site-id (:value @site-settings-id))
        (put! active-channel site-id)))))


(defn set-active-channel [current-channel]
  (swap! active-page assoc :channel current-channel))


(defn remove-site-selector-prompt
  []
  (if (= 0 (:site_settings_id (first (:options @site-settings-data))))
    (swap! site-settings-data assoc :options (rest (:options @site-settings-data)))))


(defn change-destination
  [site-id]
  (remove-site-selector-prompt)
  (swap! site-settings-id assoc :value site-id)
  
  (if (not (nil? @active-page))
    (if (not (:independent @active-page))
      (set! (.-hash js/window.location) (str "#/site/" site-id (:url @active-page))))
    (set! (.-hash js/window.location) (str "#/site/" site-id "/dashboard"))))


(defn selector-option [option]
  [:option {:value (:site_settings_id option)
            :key (:site_settings_id option)} 
        (:site_name option)])


(defn site-selector
  []
   [:select {:class "form-control"
             :value (:value @site-settings-id)
             :on-change #(change-destination (js/parseInt (-> % .-target .-value)))
             :id (:id @site-settings-data) :key "site-selector"
             }
    (doall (map selector-option (:options @site-settings-data)))])


(defn set-site
  [site-id]
  ;;(.log js/console site-id)
  (do
    (put! admin-menu-channel site-id)
    (swap! site-settings-id assoc :value site-id)
    (activate-channel site-id)
    (remove-site-selector-prompt)))


(defn init-site-selector-channel []
  (go (while true
         (set-site (<! site-selector-channel)))))


(defn get-initial-site-settings-data []
  (ajax/get-json (site-source)
                 nil 
                 (fn [response]
                   (swap! site-settings-data assoc :options (cons {:site_settings_id 0 :site_name "Select site" } response))
                   (init-site-selector-channel))))


(defn reload-site-settings-data []
  (ajax/get-json (site-source)
                 nil 
                 (fn [response]
                   (swap! site-settings-data assoc :options response))))


(defn render-site-selector []
  (ui/render site-selector "site-selector")
  (get-initial-site-settings-data))
