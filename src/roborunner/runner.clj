(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [roborunner.websockets :as ws]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))


(def battle-dir (System/getenv "ROBORUNNER_BATTLE_DIR"))
(def robot-dir (System/getenv "ROBORUNNER_ROBOT_DIR"))


(defn- calculate-battle-scores
  [results]
  {:battles results})


(defn read-battle-results
  [n]
  (let [results-folder "/tmp/roborunner/"
        result-file (str results-folder n ".json")
        f (io/file result-file)]
    (when (.exists f)
      (json/read-str (slurp f)))))


(defn run
  ([battle-folder robots-folder]
   (let [bots (bots/get-bots robots-folder)]
     (map io/delete-file (.listFiles (io/file battle-folder)))
     (battle/create-battles bots battle-folder)
     (->> (battle/get-battle-files battle-folder)
          (ws/map-notify battle/run-battle)
          calculate-battle-scores
          battle/post-battle-cleanup)))
  ([]
   (run battle-dir robot-dir)))

