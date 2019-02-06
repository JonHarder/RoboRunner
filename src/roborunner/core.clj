(ns roborunner.core
  (:require [roborunner.runner :as runner])
  (:gen-class))


;; run each battle for each battle file, store results
;; once you have results, figure out accumulated points for each bot
;;             with round multiplier (r1 = 1.0, r2 = 1.5, r3 = 2.0)
;; generate standings

;; optional v2
;; front end standings
;; specific match replayer

(defn- usage
  []
  (println "USAGE: roborunner BATTLE_FOLDER ROBOTS_FOLDER"))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [battle-folder (first args)
        robots-folder (second args)]
    (if (and battle-folder robots-folder)
      (doseq [result (runner/run battle-folder robots-folder)]
        (println result))
      (usage))))
