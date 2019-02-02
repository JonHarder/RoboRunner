(ns roborunner.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
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


(def bot-dir "/Users/jharder/bots/")


(defn get-bots
  "Gets the list file objects representing the bots found"
  [dir]
  (.listFiles (io/file dir)))


(defn bot-name
  [bot-file]
  (-> bot-file
      (.getName)
      (str/split #"\.")
      (first)))


(defn arrange-single-pairs
  [item col]
  (map (fn [col-item] (set [item col-item])) col))


(defn unique-pairs
  [col]
  (let [col-set (set col)]
    (into #{}
          (reduce (fn [pairs item]
                    (let [col-without-item (clojure.set/difference col-set (set [item]))
                          item-pairs (arrange-single-pairs item col-without-item)]
                      (clojure.set/union pairs item-pairs)))
                  #{}
                  col))))
              

(defn generate-battle-file
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


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (.mkdir (io/file bot-dir))
  ;; (let [bots (->> bot-dir
  ;;                 (get-bots)
  ;;                 (map bot-name))
  ;;       pairs (unique-pairs bots)]
  ;;   (doseq [pair pairs]
  ;;     (println pair))))
  (println (generate-battle-file "sample.bot1" "sample.bot2"))
  (let [bots ["bot1" "bot2" "bot3"]]
    (doseq [pair (unique-pairs bots)]
      (println pair))))
