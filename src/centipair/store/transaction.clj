(ns centipair.store.transaction
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))


(def payment-channel (chan))


(defn process-payment [token]
  (let [sleep-time (rand 10)] 
    (Thread/sleep sleep-time)
    (println sleep-time)
    (println (str "finished - " token))))


(defn init-payment-channel []
  (go (while true
        (process-payment (<! payment-channel)))))


(defn add-to-payment-channel [token]
  (println (str "added - " token))
  (go (>! payment-channel token)))


(defn test-go-channels []
  (doseq [x (range 1000000)] 
    (add-to-payment-channel x)))
