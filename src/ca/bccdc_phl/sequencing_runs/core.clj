(ns ca.bccdc-phl.sequencing-runs.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [integrant.core :as ig]
            [ca.bccdc-phl.sequencing-runs.system :as system]
            [ca.bccdc-phl.sequencing-runs.cli :as cli])
  (:gen-class))

(defn -main
  [& args]
  (let [opts (parse-opts args cli/options)]
    
    (when (not (empty? (:errors opts)))
      (cli/exit 1 (str/join \newline (:errors opts))))

    (when (get-in opts [:options :help])
      (let [options-summary (:summary opts)]
        (cli/exit 0 (cli/usage options-summary))))

    (-> system/system-config
        (assoc-in [:ca.bccdc-phl.sequencing-runs.system/config :path] (get-in opts [:options :config]))
        ig/init)))
