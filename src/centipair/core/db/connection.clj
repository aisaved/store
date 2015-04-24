(ns centipair.core.db.connection
  (:use korma.db)
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql])
  )

(defdb db (postgres {:db "store"
                     :user "centipair"
                     :password "c3nt1p@1r"}))


;; Will connect to cassandra at localhost
(defn get-db-connection []
  (let [conn (cc/connect ["127.0.0.1"])]
    (cql/use-keyspace conn "core")
    conn))


(def conn (atom nil))


(defn dbcon []
  (if (nil? @conn) 
    (reset! conn (get-db-connection))
    @conn))
