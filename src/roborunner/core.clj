(ns roborunner.core
  (:require [roborunner.bots :as bots]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [clojure.data.json :as json])
  (:gen-class))


;; run each battle for each battle file, store results
;; once you have results, figure out accumulated points for each bot
;;   with round multiplier (r1 = 1.0, r2 = 1.5, r3 = 2.0)
;; generate standings
;;   - cumulative points? are points comparable between battles?
;;   - stricly off of number of wins?  points don't really matter then...

;; optional v2
;; front end standings
;; specific match replayer


(defn response [data & [status]]
  "Take some data and serialize it into a response body containing json"
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})


(defroutes routes
  (GET "/standings"
      []
    (response "" 204))
  (GET "/robots"
      []
    (response (map bots/bot-name (bots/get-bots))))
  (POST "/battles"
      []
    (response {:message "battle started"} 201))
  (route/not-found
   (response {:message "not found"} 404)))


(def app
  (-> routes
      wrap-json-params))
