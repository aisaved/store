(ns centipair.store.dashboard
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [centipair.core.ui :as ui]
            [cljs.core.async :refer [put! chan <!]]
            [centipair.admin.channels :refer [set-active-channel
                                              dashboard-channel]]))


(def store-stats (atom {:id "store-stats" :label "Store stats"}))


(defn create-dashboard []
  [:div {:id (:id @store-stats)} (:label @store-stats)])


(defn render-dashboard [site-id]
  (set-active-channel dashboard-channel)
  (ui/render create-dashboard "content"))


(defn fetch-dashboard [site-id]
  (.log js/console "Dashboard fetched"))

(defn init-dashboard-channel []
  (go (while true
         (fetch-dashboard (<! dashboard-channel)))))

