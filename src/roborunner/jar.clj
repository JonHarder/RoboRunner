(ns roborunner.jar
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(defn jar-files
  [jar-file]
  (->> jar-file
       (shell/sh "jar" "-tf")
       (:out)
       (str/split-lines)))

(defn find-file-in-jar
  [jar-file re]
  (let [files (jar-files jar-file)]
    (first (filter #(re-find re %) files))))


(defn read-jar-file
  [jar-file file-path]
  (:out (shell/sh "unzip" "-q" "-c" jar-file file-path)))
