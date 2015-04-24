(ns centipair.admin.action
  (:require [centipair.admin.channels :refer [site-settings-id]]))


(defn crud-label
  [key props]
  (if (nil? (:label (key props)))
    (case key
      :create "Create"
      :delete "Delete"
      )
    (:label (key props))))


;;Example
(comment 
  {:create {:entity "entity" :label "Create Entity" }
   :delete {:action delete-function :label "Delete Entity"}})

(defn crud-action-bar
  [props]
  [:div {:id "crud-toolbar" :class "action-bar"}
   [:a {:href (str "#/site/" (:value @site-settings-id) "/" (:entity (:create props)) "/create") 
        :class "btn btn-primary"}
    (crud-label :create props)] " "
   [:a {:href "javascript: void(0)"
        :on-click (:action (:delete props))
        :class "btn btn-danger"}
    (crud-label :delete props)
    ]])


(defn render-reset-button [reset-function]
  [:a {:href "javascript:void(0)" 
       :on-click reset-function
       :class "btn btn-danger"} "Reset"])

(defn cancel-action-bar
  [site-settings-id redirect-url &[reset-function]]
  [:div {:id "crud-toolbar"
         :class "action-bar"}
   [:a {:href (str "#/site/" (:value @site-settings-id) "/" redirect-url)
        :class "btn btn-primary"} "Cancel"]
   "  "
   (if (nil? reset-function)
     ""
     (render-reset-button reset-function)
     )])

