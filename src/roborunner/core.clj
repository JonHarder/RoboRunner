(ns roborunner.core
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


;; example battle file

;; #Battle Properties
;; robocode.battleField.width=800
;; robocode.battleField.height=600
;; robocode.battle.numRounds=36
;; robocode.battle.gunCoolingRate=0.1
;; robocode.battle.rules.inactivityTime=450
;; robocode.battle.selectedRobots=BOT1,BOT2
;; robocode.battle.hideEnemyNames=false
;; robocode.battle.initialPositions=(50,50,0),(?,?,?)



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
