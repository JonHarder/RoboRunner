(ns roborunner.bots
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [roborunner.jar :as jar]))


(def bot-dir "/Users/jharder/robocode/robots")


(defn get-bots
  "Returns a list of absolute file paths to all of the bots"
  ([]
   (get-bots bot-dir))
  ([dir]
   (.mkdir (io/file bot-dir))
   (let [files (file-seq (io/file dir))
         jar-files (filter #(.endsWith (.getName %) ".jar") files)]
     (map #(.getAbsolutePath %)
          jar-files))))


(defn bot-name
  [bot-file]
  (-> bot-file
      io/file
      .getName
      (str/split #"\.")
      first))


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
