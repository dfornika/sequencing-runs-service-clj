(ns ca.bccdc-phl.sequencing-runs.crud
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [taoensso.timbre :as timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]])
  (:refer-clojure :exclude [read update]))


(defn read
  ""
  ([datasource table]
   (->> {:select :* :from table}
       (sql/format)
       (jdbc/execute! datasource)))
  ([datasource table id-key id]
   (->> {:select :* :from table :where [:= id-key id]}
        (sql/format)
        (jdbc/execute! datasource))))


(defn create!
  ""
  [datasource table id-key entity]
  (let [entity-id (get entity id-key)
        existing-entity (read datasource table id-key entity-id)
        columns (keys entity)
        values [(vals entity)]]
    (when (empty? existing-entity)
      (->> {:insert-into [table]
            :columns columns
            :values values}
           (sql/format)
           (jdbc/execute! datasource)))))


(defn update!
  ""
  [datasource table id-key entity]
  )


(defn delete!
  ""
  [datasource table id-key id]
  (->> {:delete-from [table]
        :where [:= id-key id]}
       (sql/format)
       (jdbc/execute! datasource)))
