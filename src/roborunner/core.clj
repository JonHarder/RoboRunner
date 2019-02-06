(ns roborunner.core
  (:require [roborunner.battle :as battle]
            [roborunner.bots :as bots]
            [clojure.java.io :as io])
  (:gen-class))


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
  (let [battle-folder "/Users/jharder/robocode/battles"
        bots (bots/get-bots)]
    (battle/create-battles bots battle-folder)
    (let [battle-files (.list (io/file battle-folder))]
      (doseq [battle-file battle-files]
        (println (battle/parse-battle-results (battle/run-battle battle-file)))))))
