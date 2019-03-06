(ns roborunner.core
  (:require [roborunner.bots :as bots]
            [roborunner.runner :as runner]
            [roborunner.battle :as battle]
            [roborunner.websockets :refer [ws-handler]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.data.json :as json]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))


(defn response [data & [status]]
  "Take some data and serialize it into a response body containing json"
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})


(defroutes routes
  (GET "/robots"
    []
    (->> (bots/get-bots)
         (map bots/bot-name)
         (response)))

  (POST "/battles"
    []
    (let [battle-id (inc (battle/num-battles))
          link (str "/battles/" battle-id)]
      (future (runner/run))
      (response {:message "battle started" :forward link} 201)))

  (GET "/battles/:id"
    [id]
    (response (runner/read-battle-results id)))

  (GET "/download/:name"
    [name]
    (bots/get-bot-stream name))

  (GET "/ws"
    request
    (ws-handler request))
    
  (route/not-found
    (response {:message "not found"} 404)))


(def app
  (-> routes
      wrap-json-params
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-credentials "true"
                 :access-control-allow-methods [:get :post])))


(defn -main
  [& args]
  (let [port 3000]
    (println "server started!")
    (println (str "listening on localhost:" port))
    (run-server app {:port port})))
