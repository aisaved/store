(ns centipair.admin.url
  (:require [centipair.admin.channels :refer [site-settings-id]]))


(defn entity-url [hash-url entity-id]
  (set! (.-hash js/window.location) 
           (str "#/site/" (:value @site-settings-id) hash-url "/" entity-id)))
