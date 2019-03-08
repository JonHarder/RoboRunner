(ns roborunner.jar
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]))

(defn jar-files
  [jar-file]
  (->> jar-file
       (shell/sh "jar" "-tf")
       (:out)
       (str/split-lines)
       (filter seq)))


(defn find-file-in-jar
  [jar-file re]
  (let [files (jar-files jar-file)]
    (first (filter #(re-find re %) files))))


(defn read-jar-file
  "Tries to read the contents of a particular file in the JAR-FILE specified by FILE-PATH.
  Returns nil if the file couldn't be found."
  [jar-file file-path]
  (try (:out shell/sh "unzip" "-q" "-c" jar-file file-path)
       (catch Exception e (do (println "Error:" jar-file "is not a valid Robocode jar file") nil))))
