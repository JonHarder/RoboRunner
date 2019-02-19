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


(defn update-json
  [data]
  (notify-clients (json/write-str data)))

