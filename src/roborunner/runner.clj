(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [clojure.java.io :as io]))


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
         calculate-battle-scores)))

