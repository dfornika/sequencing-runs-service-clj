(ns ca.bccdc-phl.sequencing-runs.crud
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [com.brunobonacci.mulog :as u])
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
      (let [sql-statement (->> {:insert-into [table]
                                :columns columns
                                :values values}
                               (sql/format))]
            (try
              (jdbc/execute! datasource sql-statement)
              (catch java.sql.SQLException e (u/log ::create! :exception e :status :failed)))))))


(defn update!
  ""
  [datasource table id-key entity]
  )


(defn delete!
  ""
  [datasource table id-key id]
  (->> {:delete-from [table]
        :where [:= id-key id]}
       (#(sql/format % {:inline true}))
       (jdbc/execute! datasource)))
