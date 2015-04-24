(ns centipair.store.cache
  (require [immutant.caching :as cache]))


(def store-cache (cache/cache "store-cache"))



;;{:1 {:stock 60 :name "Item 1"}}




(defn mock-cache-data-structure []
  (reduce (fn [previous next]
            (assoc previous
                   (keyword  (str next))
                   {:stock (rand-int 100) :name (str "Item - " next)}
                   ))
          {}
          (range 1000000)))

(defn init-store-cache []
  (.putAll store-cache (mock-cache-data-structure)))


(defn mock-transaction-test []
  
  )
