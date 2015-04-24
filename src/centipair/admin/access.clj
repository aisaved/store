(ns centipair.admin.access
  (:require [centipair.core.contrib.cookies :refer [get-auth-token]]
            [centipair.core.auth.user.models :as user-models]))


(defn is-admin?
  "checks for admin acces"
  [request]
  (let [auth-token (get-auth-token request)
        user-account (user-models/get-user-session auth-token)]
    (if (:is_admin user-account)
    true
    false)))

