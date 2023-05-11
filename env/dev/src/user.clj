(ns user
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [ragtime.jdbc]
            [ragtime.repl]
            [integrant.repl]
            [integrant.core]
            [cheshire.core :as json]
            [ca.bccdc-phl.sequencing-runs.config :as config]
            [ca.bccdc-phl.sequencing-runs.system :as system]
            [ca.bccdc-phl.sequencing-runs.crud :as crud]))

(def config-path "dev-config.edn")

(def app-config
  (config/load-config config-path))


(def ragtime-config
  {:datastore  (ragtime.jdbc/sql-database (:db app-config))
   :migrations (ragtime.jdbc/load-resources "migrations")})



(integrant.repl/set-prep! (fn []
                            (-> system/system-config
                                (assoc-in [:ca.bccdc-phl.sequencing-runs.system/config :path] config-path)
                                integrant.core/prep)))


(def prep integrant.repl/prep)
(def init integrant.repl/init)
(def go integrant.repl/go)
(def halt integrant.repl/halt)
(def reset integrant.repl/reset)
(def reset-all integrant.repl/reset-all)


(def dev-instruments-illumina
  (-> (io/resource "illumina_instruments.json")
      (slurp)
      (json/parse-string true)))

(defn create-instruments-illumina
  ""
  [instruments]
  (let [ds (get integrant.repl.state/system :ca.bccdc-phl.sequencing-runs.system/db)]
    (doseq [instrument instruments]
      (crud/create ds :sequencing_instrument_illumina :instrument_id instrument))))

(comment
  (ragtime.repl/migrate ragtime-config)
  (ragtime.repl/rollback ragtime-config)
  integrant.repl.state/system
  (go)
  (halt)
  (reset)
  (reset-all)

  (create-instruments-illumina dev-instruments-illumina)
  )
