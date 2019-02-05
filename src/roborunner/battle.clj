(ns roborunner.battle
  (:require [roborunner.pairs :as pairs]
            [roborunner.bots :as bots]
            [clojure.java.io :as io]))


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
    (map io/delete-file (.listFiles (io/file battle-folder)))
    (spit file-name battle-data)))


(defn create-battles
  [bot-jars battle-folder]
  (let [pairings (pairs/unique-pairs bot-jars)]
    (map (partial write-battle-file battle-folder) pairings)))

    


      
