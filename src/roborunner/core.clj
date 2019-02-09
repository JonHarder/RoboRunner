(ns roborunner.core
  (:require [roborunner.bots :as bots]
            [roborunner.runner :as runner]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.data.json :as json])
  (:gen-class))


;; once you have results, figure out accumulated points for each bot
;;   with round multiplier (r1 = 1.0, r2 = 1.5, r3 = 2.0)

;; posting to battles should create a new battle with its own scores
;; get /battles/:id should give you battle results for that battle

;; optional v2
;; specific match replayer


(defn response [data & [status]]
  "Take some data and serialize it into a response body containing json"
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/write-str data)})


(defroutes routes
  (GET "/standings"
      []
    (if-let [battle-results (runner/read-battle-results)]
      (response battle-results)
      ""))
  (GET "/robots"
      []
    (response (map bots/bot-name (bots/get-bots))))
  (POST "/battles"
      []
    (future (runner/run
              "/Users/jharder/robocode/battles"
              "/Users/jharder/robocode/robots"))
    (response {:message "battle started" :forward "/standings"} 201))
  (route/not-found
   (response {:message "not found"} 404)))


(def app
  (-> routes
      wrap-json-params
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-credentials "true"
                 :access-control-allow-methods [:get :post])))
