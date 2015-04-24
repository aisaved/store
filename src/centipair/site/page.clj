(ns centipair.site.page
  (:use centipair.site.models)
  (:require [validateur.validation :refer :all]
            ))



(def page-validator
  (validation-set
   (presence-of :page-title :message "This page needs a title")
   (presence-of :page-url :message "This page needs a URL")
   ))


(defn unique-page-url-valid [params]
  (let [page-data (select-site-page (:site-settings-id params) (:page-url params))]
    (if (nil? page-data)
      true
      (if (= (:page_id page-data) (:page-id params))
        true
        [false {:validation-result {:errors {:page-url ["Another page have the same url"]}}}]))))


(defn page-exists? [id]
  (not (nil? (select-page id))))


(defn get-all-pages [params]
  (if (nil? (:site-settings-id params))
    []
    (select-site-pages params)))

(defn get-page [id]
  (select-page id))

;;TODO: make sure url is unique and is of supported format
(defn validate-page
  [params]
  (let [validation-result (page-validator params)]
    (if (valid? validation-result)
      (unique-page-url-valid params)
      [false {:validation-result {:errors validation-result}}])))


(defn save-page
  [params]
  (if (nil? (:page-id params))
    {:created (create-page params)}
    (do (update-page params)
        {:created (select-page (:page-id params))})))


(defn remove-page
  [params]
  (delete-page (:id params)))


(defn fetch-site-page
  [params]
  (select-site-page (:site-id params) (:url params)))
