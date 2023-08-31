(ns user
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]]
            [ragtime.jdbc]
            [ragtime.repl]
            [integrant.repl]
            [integrant.core]
            [cheshire.core :as json]
            [next.jdbc]
            [ca.bccdc-phl.sequencing-runs.config :as config]
            [ca.bccdc-phl.sequencing-runs.system :as system]
            [ca.bccdc-phl.sequencing-runs.crud :as crud])
  (:import [java.time LocalDate]))

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
      (crud/create! ds :sequencing_instrument_illumina :instrument_id instrument))))


(defn delete-instruments-illumina
  ""
  [instruments]
  (let [ds (get integrant.repl.state/system :ca.bccdc-phl.sequencing-runs.system/db)]
    (doseq [instrument instruments]
      (crud/delete! ds :sequencing_instrument_illumina :instrument_id (:instrument_id instrument)))))


(def dev-instruments-nanopore
  (-> (io/resource "nanopore_instruments.json")
      (slurp)
      (json/parse-string true)))


(def dev-sequencing-runs-illumina
  (->> (io/resource "illumina_sequencing_runs.json")
      (slurp)
      (#(json/parse-string % true))
      (map (fn [run] (update run :run_date #(LocalDate/parse %))))
      (map #(set/rename-keys % {:id :sequencing_run_id}))
      ))


(defn create-sequencing-runs-illumina
  ""
  [sequencing-runs]
  (let [ds (get integrant.repl.state/system :ca.bccdc-phl.sequencing-runs.system/db)]
    (doseq [sequencing-run sequencing-runs]
      (let [run (dissoc sequencing-run :demultiplexings)
            demultiplexings (:demultiplexings sequencing-run)]
      (next.jdbc/with-transaction [tx ds]
        (crud/create! ds :sequencing_run_illumina :sequencing_run_id run)
        (doseq [demux demultiplexings]
          (crud/create! ds :sequencing_run_illumina_demultiplexing :demultiplexing_id demux)
        ))))))

(def system integrant.repl.state/system)


(comment
  (ragtime.repl/migrate ragtime-config)
  (ragtime.repl/rollback ragtime-config)
  
  (go)
  (halt)
  (reset)
  (reset-all)

  (create-instruments-illumina dev-instruments-illumina)
  (delete-instruments-illumina dev-instruments-illumina)

  (create-sequencing-runs-illumina dev-sequencing-runs-illumina)
  )
