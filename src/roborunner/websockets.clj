(ns roborunner.websockets
  (:require [org.httpkit.server
             :refer [send! with-channel on-close on-receive]]
            [clojure.data.json :as json]))


(defonce channels (atom #{}))


(defn- notify-clients
  [msg]
  (doseq [channel @channels]
     (send! channel msg)))


(defn- connect!
  [channel]
  (println "channel open")
  (swap! channels conj channel))


(defn- disconnect!
  [channel status]
  (println (str "channel closed: " status))
  (swap! channels #(remove #{channel} %)))


(defn ws-handler
  [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients %))))



(defn- update-json
  [data]
  (notify-clients (json/write-str data)))


(defn send-message
  ([message-type]
   (send-message message-type nil))
  ([message-type data]
   (update-json
    {:message message-type
     :data data})))


(defn- update-progress
  [current total]
  (send-message "progress"
                {:complete current
                 :out-of total}))


(defn map-notify
  [fn_ coll]
  (let [total (count coll)]
    (update-progress 0 total)
    (map-indexed
     (fn [idx x]
       (let [new-x (fn_ x)]
         (update-progress (inc idx) total)
         new-x))
     coll)))
