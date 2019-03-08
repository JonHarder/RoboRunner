(ns roborunner.bots
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [roborunner.jar :as jar]
            [roborunner.websockets :as ws]))


(def bot-dir "/Users/jharder/robocode/robots")


(defn get-bot-stream
  [bot-name]
  (io/input-stream (str bot-dir "/" bot-name)))


(defn save-bot
  [name bot-byte-stream]
  (let [out-file (str bot-dir "/" name)]
    (spit out-file bot-byte-stream)
    (ws/send-message :bot-uploaded)))


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
      (str/replace #"(\w+)\.jar$" "$1")))


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
    (and (not (empty? prop-file))
         (jar/read-jar-file jar-file prop-file))))


(defn get-classname
  [jar-file]
  (let [props-file (read-properties-file jar-file)
        props (and props-file (parse-properties-file props-file))]
    (and props (get props "robot.classname"))))


(defn valid-bot?
  [bot-file]
  (string? (get-classname bot-file)))


(defn gather-bot-info
  [bot-jar]
  {:name (bot-name bot-jar)
   :class (get-classname bot-jar)
   :path bot-jar})
