(ns roborunner.pairs
  (:require [clojure.set]))


(defn- arrange-single-pairs
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
