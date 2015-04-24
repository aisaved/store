(ns centipair.admin.resources)


(def site-endpoint-url "/admin/api/site")
(def store-endpoint-url "/admin/api/store")
(def page-endpoint-url "/admin/api/page")
(def user-endpoint-url "/admin/api/user")


(defn site-source [&[id]]
  (if (nil? id)
    site-endpoint-url
    (str site-endpoint-url "/" id)))


(defn store-source [&[id]]
  (if (nil? id)
    store-endpoint-url
    (str store-endpoint-url "/" id)))


(defn page-source [&[id]]
  (if (nil? id)
    page-endpoint-url
    (str page-endpoint-url "/" id)))


(defn user-source [&[id]]
  (if (nil? id)
    user-endpoint-url
    (str user-endpoint-url "/" id)))

