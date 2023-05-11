(ns ca.bccdc-phl.sequencing-runs.crud
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql])
  (:refer-clojure :exclude [read]))

(defn create
  [datasource table entity]
  (let [columns (keys entity)
        values (vals entity)]
    (->> {:insert-into [table]
          :columns columns
          :values values}
         (sql/format)
         (jdbc/execute! datasource))))

(defn read
  [datasource table]
  (->> {:select :* :from table}
       (sql/format)
       (jdbc/execute! datasource)))
