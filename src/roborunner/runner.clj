(ns roborunner.runner
  (:require [roborunner.bots :as bots]
            [roborunner.battle :as battle]
            [clojure.java.io :as io]))


(defn run
  [battle-folder robots-folder]
  (let [bots (bots/get-bots robots-folder)]
    (battle/create-battles bots battle-folder)
    (let [battle-files (.list (io/file battle-folder))]
      (map battle/run-battle battle-files))))

