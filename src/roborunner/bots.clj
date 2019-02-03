(ns roborunner.bots
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [roborunner.jar :as jar]))


(def bot-dir "/Users/jharder/bots/")


(defn get-bots
  "Gets the list file objects representing the bots found"
  [dir]
  (.mkdir (io/file bot-dir))
  (map #(.getAbsolutePath %)
       (.listFiles (io/file dir))))


(defn bot-name
  [bot-file]
  (-> bot-file
      (io/file)
      (.getName)
      (str/split #"\.")
      (first)))


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


(defn make-equals-pair
  [string]
  (let [[k v] (str/split string #"=")]
    {k v}))


(defn parse-properties-file
  "a string with newlines containing the contents of a robocode properties file"
  [property-str]
  (let [lines (str/split-lines property-str)]
    (reduce (fn [props line]
              (into props (make-equals-pair line)))
            {}
            lines)))

(defn read-properties-file
  [jar-file]
  (let [prop-file (jar/find-file-in-jar jar-file #"\.properties$")]
    (jar/read-jar-file jar-file prop-file)))


(defn get-classname
  [jar-file]
  (let [props-file (read-properties-file jar-file)
        props (parse-properties-file props-file)]
    (get props "robot.classname")))


(defn gather-bot-info
  [bot-jar]
  {:name (bot-name bot-jar)
   :class (get-classname bot-jar)
   :path bot-jar})
