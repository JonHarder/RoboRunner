(ns roborunner.battle
  (:require [roborunner.pairs :as pairs]
            [roborunner.bots :as bots]))


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


(defn create-battles
  [bot-jars]
  (let [pairings (pairs/unique-pairs bot-jars)]
    (map (fn [pairing]
           (let [bot1 (bots/get-classname (first pairing))
                 bot2 (bots/get-classname (second pairing))]
             (generate-battle-file bot1 bot2)))
         pairings)))

    


      
