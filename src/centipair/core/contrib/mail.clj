(ns centipair.core.contrib.mail
  (:use postal.core
        centipair.settings
        centipair.secret
        selmer.parser))

;;email settings should be defined in centipair.core.secret
;;(def email-settings {:host "smtphost.server.com"
;;                     :user "username"
;;                     :pass "password"
;;                     :port 123})
;;

(defn send-registration-email [registration-request]
  (let [site-data site-settings
        email-body (render-file "email/registration.html" 
                                (assoc site-data :registration-key (str (:registration_key registration-request))))]
    (send-message email-settings
                  {:from (:email site-data)
                   :to (:email registration-request)
                   :subject (str "Please activate your " (:name site-data) " account")
                   :body [{:type "text/html"
                           :content email-body}]})))

(defn send-password-reset-email [email password-reset-key]
  (let [site-data site-settings
        email-body (render-file "email/password-reset.html" 
                                (assoc site-data :reset-key password-reset-key))]
    (send-message email-settings
                  {:from (:email site-data)
                   :to email
                   :subject (str "Reset your " (:name site-data) " password")
                   :body [{:type "text/html"
                           :content email-body}]})))
