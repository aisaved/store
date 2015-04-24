(ns centipair.core.auth.user.nosql
    (:use 
     centipair.core.db.connection
     centipair.core.contrib.time
     centipair.core.contrib.mail
     clojurewerkz.cassaforte.cql
     clojurewerkz.cassaforte.query
     clojurewerkz.cassaforte.uuids
     [centipair.core.utilities.validators :only [is-username? is-email-proxy?]])
    (:require 
     [centipair.core.contrib.cryptography :as crypto]
     [centipair.core.errors :as errors]))


(def early-access-table "early_access")
(def user-account-table "user_account")
(def user-login-username-table "user_login_username")
(def user-login-email-table "user_login_email")
(def user-session-table "user_session")
(def user-session-index-table "user_session_index")
(def user-account-registration-table "user_account_registration")
(def user-profile-table "user_profile")
(def password-reset-table "password_reset")

(def login-error {:status-code 422 :errors {:login-form ["Username or password incorrect"]}})
(def inactive-user-error {:status-code 422 :errors {:login-form ["This account is inactive. Please activate your account."]}})


(defn insert-user-session [session-map]
  (do
    (insert (dbcon) user-session-table session-map)
    (insert (dbcon) user-session-index-table {:user_id (:user_id session-map)
                                              :auth_token (:auth_token session-map)})))


(defn create-user-session [user-account]
  (let [auth_token (crypto/generate-session-id)
        session-map {:auth_token auth_token
                     :session_expire_time (set-time-expiry 23)
                     :user_id (:user_id user-account)}]
    (insert-user-session session-map)
    auth_token))

(defn get-user-session [auth-token]
  (let [auth_token auth-token]
    (if (nil? auth_token)
      nil
      (first (select (dbcon) user-session-table (where [[= :auth_token auth_token]]))))))

(defn session-user-id [] 
  (:user_id (get-user-session)))

(defn delete-session-index [user_session]
  (delete (dbcon) user-session-index-table (where 
                                            [[= :user_id (:user_id user_session)]
                                             [= :auth_token (:auth_token user_session)]])))

(defn delete-session [user_session]
  (do
    (delete (dbcon) user-session-table (where [[= :auth_token (:auth_token user_session)]]))
    (delete-session-index user_session)))

(defn destroy-session []
  (let [user_session (get-user-session)]
    (if (nil? user_session)
      nil
      (delete-session user_session))))

(defn select-user-account [user_id]
  (select (dbcon) user-account-table (where [[= :user_id user_id]])))


(defn insert-user-login-username [user-map]
  (insert (dbcon) user-login-username-table {:user_id (:user_id user-map)
                                             :username (:username user-map)}))

(defn insert-user-login-email [user-map]
  (insert (dbcon) user-login-email-table {:user_id (:user_id user-map)
                                       :email (:email user-map)}))

(defn insert-user-account [user-map]
  (let [user_id (time-based)]
    (insert (dbcon) user-account-table {:user_id user_id
                                        :username (:username user-map)
                                        :email (:email user-map)
                                        :first_name (:first_name user-map)
                                        :last_name (:last_name user-map)
                                        :active (:active user-map)
                                        :password (crypto/encrypt-password (:password user-map))})
    user_id))

(defn insert-registration-request [user-map]
  (insert (dbcon) user-account-registration-table {:registration_key (:registration_key user-map)
                                                   :user_id (:user_id user-map)}))


(defn register-user
  [user-map]
  (let [user_id (insert-user-account (assoc user-map :active false))
        user-login-map {:user_id user_id
                        :email (:email user-map)
                        :username (:username user-map)
                        :registration_key (time-based)}]
    (do 
      (insert-user-login-username user-login-map)
      (insert-user-login-email user-login-map)
      (insert-registration-request user-login-map)
      (future (send-registration-email user-login-map)) ;;TODO: Track this
      {:status-code 200 :message "registration success"})))


(defn select-user-username [username]
  (first (select (dbcon) user-login-username-table (where [[= :username username]]))))


(defn select-user-email [email]
  (first (select (dbcon) user-login-email-table (where [[= :email email]]))))

(defn get-user-login [username]
  (if (is-username? username)
    (select-user-username username)
    (if (is-email-proxy? username)
      (select-user-email username)
      nil)))

(defn get-user-account [user-login]
  (if (nil? user-login)
    nil
    (first (select-user-account (:user_id user-login)))))

(defn valid-user-password? [user-account form]
  (if (nil? (:password form))
    false
    (crypto/check-password (:password form) (:password user-account))))


(defn login [params]
  (let [user-login (get-user-login (:username params))
        user-account (get-user-account user-login)]
    
    (create-user-session user-account)))


(defn check-login
  [params]
  (let [user-login (get-user-login (:username params))
        user-account (get-user-account user-login)]
    (if (or (nil? user-login) (nil? user-account))
      (errors/validation-error :username "User account not found")
      (if (:active user-account)
        (if (valid-user-password? user-account params)
          true
          (errors/validation-error :password "Password is wrong"))
        (errors/validation-error :username "User account inactive")))))


(defn activate-user-account [user-map]
  (update (dbcon) user-account-table {:active true} (where [[= :user_id (:user_id user-map)]])))

(defn get-registration-key [registration-key]
  (first (select (dbcon) user-account-registration-table (where [[= :registration_key registration-key]]))))

(defn delete-activation-key [user-map]
  (delete (dbcon) user-account-registration-table (where [[= :registration_key (:registration_key user-map)]])))

(defn activate-account [registration-key]
  (let [registration-request (get-registration-key (crypto/str-uuid registration-key))]
    (if (nil? registration-request)
      false
      (do 
        (activate-user-account registration-request)
        (delete-activation-key registration-request)
        (:user_id registration-request)))))

(defn insert-early-access-email [form]
  (insert (dbcon) early-access-table {:email (:email form)})
  {:status-code 200 :message "email saved"})



(defn delete-account-origin [user-id]
  (let [user-account (first (select user-account-table (where :user_id user-id)))
        username (:username user-account)
        auth-tokens (into [] (map (fn [x] (:auth_token x))(select user-session-index-table (where :user_id user-id))))]
    (do
      (delete (dbcon) user-login-username-table (where :username username))
      (delete (dbcon) user-login-email-table (where :email (:email user-account)))
      (delete (dbcon) user-session-index-table (where :user_id user-id))
      (delete (dbcon) user-session-table (where :auth_token [:in auth-tokens]))
      (delete (dbcon) user-account-table (where :user_id user-id ))
      (delete (dbcon) user-profile-table (where :user_id user-id ))
    )
    "deleted everything"
  ))

(defn delete-account [email]
  (let [user-email (select-user-email email)]
    (if (nil? user-email)
      "email not found"
      (delete-account-origin (:user_id user-email)))))


(defn password-reset-email [form]
  (let [email (:email form)
        reset-key (time-based)
        user-id ((select-user-email email) :user_id)
        password-reset {:password_reset_key reset-key :user_id user-id :expiry (set-time-expiry 48)}]
    (do
      (insert (dbcon) password-reset-table password-reset)
      (future (send-password-reset-email email password-reset))
      {:status-code 200 :message "password reset"})))


(defn valid-password-reset-key [reset-key]
  (let [reset-key-uuid (crypto/str-uuid reset-key)
        db-fetch (select (dbcon) password-reset-table 
                         (where :password_reset_key 
                                reset-key-uuid))]
    (if (empty? db-fetch)
      false
      (if (time-expired? (:expiry (first db-fetch)))
        false
        true))))

(defn change-password [user-id password]
  (let [user-account (first (select-user-account user-id))]
    (update (dbcon) user-account-table {:password (crypto/encrypt-password password)} (where :user_id user-id))
    {:status-code 200 :message "Password changed"}))

(defn reset-password [form]
  (let [user-id (:user_id (first (select (dbcon) password-reset-table 
                                         (where [[= :password_reset_key (crypto/str-uuid (:reset_key form))]]))))
        password (:password form)]
  (if (= (:password form) (:confirm_password form))
    (do 
      (delete (dbcon) password-reset-table (where :password_reset_key (crypto/str-uuid (:reset_key form))))
      (change-password user-id password))
    {:status-code 422 :errors {:confirm_password "Passwords do not match"}})))


(defn userid-token [auth-token]
  (let [session (first (select (dbcon) user-session-table (where [[= :auth_token auth-token]])))]
    (if (nil? session)
      nil
      (:user_id session))))

