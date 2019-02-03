(ns roborunner.core
  (:require [roborunner.pairs :as pairs]
            [roborunner.bots :as bots])
  (:gen-class))


;; figure out all the bots
;; 36 round battle
;; generate a bunch of battle files for each pairing
;; run each battle for each battle file, store results
;; once you have results, figure out accumulated points for each bot
;;             with round multiplier (r1 = 1.0, r2 = 1.5, r3 = 2.0)
;; generate standings

;; optional v2
;; front end standings
;; specific match replayer



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [bots (->> bots/bot-dir
                  (bots/get-bots)
                  (map bots/gather-bot-info))]
    (println bots)))
