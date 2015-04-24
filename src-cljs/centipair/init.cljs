(ns centipair.init
  (:require [centipair.core.components.notifier :as notifier]
            [centipair.core.user.forms :as user-forms]
            [centipair.core.csrf :as csrf]))



(defn ^:export init-app []
  (do
    (notifier/render-notifier-component)
    (csrf/fetch-csrf-token)))
