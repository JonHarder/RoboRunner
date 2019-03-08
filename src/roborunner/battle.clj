(ns roborunner.battle
  (:require [roborunner.pairs :as pairs]
            [roborunner.bots :as bots]
            [roborunner.websockets :as websockets]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as str]))


(defn- generate-battle-file
  "this needs to know the bots project name as well somehow..."
  [bot1 bot2]
  (format "#Battle Properties
robocode.battleField.width=800
robocode.battleField.height=600
robocode.battle.numRounds=36
robocode.battle.gunCoolingRate=0.1
robocode.battle.rules.inactivityTime=450
robocode.battle.selectedRobots=%s,%s
robocode.battle.hideEnemyNames=false
robocode.battle.initialPositions=(50,50,0),(?,?,?)" bot1 bot2))


(defn write-battle-file
  [battle-folder bot-pair]
  (let [bot1 (bots/get-classname (first bot-pair))
        bot2 (bots/get-classname (second bot-pair))
        bot1-name (bots/bot-name (first bot-pair))
        bot2-name (bots/bot-name (second bot-pair))
        battle-data (generate-battle-file bot1 bot2)
        file-name (str battle-folder "/" bot1-name "_vs_" bot2-name ".battle")]
    (spit file-name battle-data)))


(defn create-battles
  [bot-jars battle-folder]
  (let [pairings (into [] (pairs/unique-pairs bot-jars))]
    (doseq [pair pairings]
      (write-battle-file battle-folder pair))))


(defn- execute-battle
  [battle-file]
  (:out (shell/sh "/Users/jharder/robocode/robocode.sh"
                  "-battle"
                  battle-file
                  "-nodisplay")))


(defn- parse-int
  [str-with-number]
  (Integer. (re-find #"[0-9]+" str-with-number)))


(defn- parse-single-result
  "takes a single battle result like: '1st: foo.Bot\t1055 (34%)\t100\t20\t900\t17\t18\t0\t3\t33\t0\t'"
  [result]
  (let [items (str/split result #"\t")
        bot-class (second (str/split (first items) #" "))
        score (-> items
                  second
                  (str/split #" ")
                  second
                  parse-int)]
    {:name bot-class :score score}))
  

(defn- parse-first
  [score-line]
  {:first (parse-single-result score-line)})


(defn- parse-second
  [score-line]
  {:second (parse-single-result score-line)})


(defn- parse-score-lines
  [battle-results]
  (->> battle-results
       str/split-lines
       reverse
       (take 2)
       reverse))


(defn- parse-battle-results
  [battle-results]
  (let [[first second] (parse-score-lines battle-results)]
    (into (parse-first first) (parse-second second))))


(defn num-battles
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
     (spit result-file (json/write-str battle-scores))
     battle-scores))


(defn- send-results
  "Sends the battle results object to all subscribed websockets."
  [results]
  (websockets/send-message "results" results))


(defn get-battle-files
  [battle-dir]
  (->> battle-dir
       io/file
       .list))


(defn post-battle-cleanup
  [results]
  (-> results
      save-battle-results
      send-results))


(defn run-battle
  [battle-file]
  (-> battle-file
      execute-battle
      parse-battle-results))
