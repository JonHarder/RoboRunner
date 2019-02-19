(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))


(def battle-dir (System/getenv "ROBORUNNER_BATTLE_DIR"))
(def robot-dir (System/getenv "ROBORUNNER_ROBOT_DIR"))


(defn- battle-pair-flatten
  [result-pair]
  (let [[f s] result-pair]
    (assoc (hash-map (:name f) (hash-map (:name s) (:score f)))
           (:name s) (hash-map (:name f) (:score s)))))


(defn- calculate-battle-scores
  "(({:name \"lunixlabs.lunixbot.LunixBot\", :score 62}
     {:name \"starterbot.StarterBot\", :score 38})
    ({:name \"jonbot.JonBot\", :score 81}
     {:name \"starterbot.StarterBot\", :score 19})
    ({:name \"lunixlabs.lunixbot.LunixBot\", :score 53}
     {:name \"jonbot.JonBot\", :score 47}))"
  [results]
  (reduce (fn [score-map pair]
            (let [flattened (battle-pair-flatten pair)]
              (merge-with into score-map flattened)))
          {}
          results))

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
     (battle/create-battles bots battle-folder)
     (->> (battle/get-battle-files battle-folder)
          (map battle/run-battle)
          calculate-battle-scores
          battle/post-battle-cleanup)
     (println "battle finished!")))
  ([]
   (run battle-dir robot-dir)))

