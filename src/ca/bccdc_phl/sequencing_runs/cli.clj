(ns ca.bccdc-phl.sequencing-runs.cli
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(def options
  [["-c" "--config CONFIG_FILE" "Config file"
    :validate [#(.exists (io/as-file %)) #(str "Config file '" % "' does not exist.")]]
   ["-h" "--help"]
   ["-v" "--version"]])


(defn usage
  ""
  [options-summary]
  (->> [(str/join " " ["sequencing-runs-service"])
        ""
        "Usage: java -jar sequencing-runs-service.jar OPTIONS"
        ""
        "Options:"
        options-summary]
       (str/join \newline)))


(defn exit
  "Exit the program with status code and message"
  [status msg]
  (println msg)
  (System/exit status))
