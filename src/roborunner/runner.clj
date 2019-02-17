(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [clojure.data.json :as json]
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

(defn- num-battles
  "Checks the battle results folder to see how many battles have been played."
  []
  (-> "/tmp/roborunner/"
      io/file
      .list
      count))


(defn- save-battle-results
  [battle-scores]
  (let [result-folder "/tmp/roborunner/"
        result-num (inc (num-battles))
        result-file (str result-folder result-num ".json")]
     (io/make-parents result-file)
     (println (str "writing results to " result-file))
     (spit result-file (json/write-str battle-scores))
     battle-scores))


(defn read-battle-results
  [n]
  (let [results-folder "/tmp/roborunner/"
        result-file (str results-folder n ".json")
        f (io/file result-file)]
    (if (.exists f)
       (json/read-str (slurp f))
      nil)))


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
         calculate-battle-scores
         save-battle-results)))

