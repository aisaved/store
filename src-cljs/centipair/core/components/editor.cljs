(ns centipair.core.components.editor
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [markdown.core :refer [md->html]]
            [cljs.core.async :refer [put! chan <!]]
            [centipair.core.style :as style]
            [centipair.core.utilities.dom :as dom]))


(def markdown-html-channel (chan))



(defn markdown-html-preview
  [field]
  [:div (:html field)])


(defn update-markdown-value
  [field value]
    (reset! field (assoc @field :value value))
    (put! markdown-html-channel [field value]))


(defn generate-preview [field-value-group]
  (let [field (first field-value-group)
        value (second field-value-group)]
    (reset! field (assoc @field :html (md->html (js/safe_tags_regex value))))
    (dom/innerHtml "markdown-preview" (:html @field))))



(defn markdown-editor
  [field]
  [:div {:key (str "markdown-editor-container" (:id @field))}
   [:ul {:class "nav nav-tabs" :key (str "markdown-editor-menu-container-" (:id @field))}
    [:li {:class "active" :role "presentation" :key (str "markdown-editor-preview-" (:id @field))}
     [:a {:href "#md-editor" 
          :aria-controls "md-editor"
          :role "tab"
          :data-toggle "tab"} "Editor"]]
    [:li {:class "" :role "presentation"}
     [:a {:href "#md-preview" 
          :aria-controls "md-preview"
          :role "tab"
          :data-toggle "tab"} "Preview"]]]
   [:div {:class "tab-content"}
    [:div {:class "tab-pane active" :role "tab-panel" :id "md-editor"}
     [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field))
            :key (str "container-" (:id @field))}
      [:label {:for (:id @field) :class "control-label" :key (str "label-" (:id @field))} (:label @field)]
      [:textarea {:class "form-control"
                  :type (:type @field) :id (:id @field)
                  :placeholder
                  (if (nil? (:placeholder @field))
                    ""
                    (:placeholder @field))
                  :rows "25"
                  :value (:value @field)
                  :on-change #(update-markdown-value field (-> % .-target .-value) )
                  :key (:id @field)
                  }]
      [:label {:class "message-label" 
               :key (str "message-" (:id @field))} (if (nil? (:message @field))
                                                     ""
                                                     (:message @field))]]]
    [:div {:class "tab-pane" :role "tab-panel" :id "md-preview"}
     [:div {:class "panel panel-default"}
      [:div {:id "markdown-preview" :class "panel-body"}]]]]])



(defn init-markdown-channel []
  (go (while true
         (generate-preview (<! markdown-html-channel)))))
