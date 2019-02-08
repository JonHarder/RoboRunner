(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [clojure.java.io :as io]))


(defn- calculate-battle-scores
  [results]
  (let [grouped-results (group-by :name results)]
    (reduce (fn [score-map [key val]]
              (let [scores (map :score val)
                    score (/ (reduce + scores) (count scores))]
                (assoc score-map key score)))
            {}
            grouped-results)))

(defn- sort-battle-results
  [battle-results]
  (sort #(compare (second %2) (second %1)) battle-results))


(defn run
  [battle-folder robots-folder]
  (let [bots (bots/get-bots robots-folder)]
    (battle/create-battles bots battle-folder)
    (->> battle-folder
         io/file
         .list
         (map battle/run-battle)
         (apply concat)
         calculate-battle-scores
         sort-battle-results)))

