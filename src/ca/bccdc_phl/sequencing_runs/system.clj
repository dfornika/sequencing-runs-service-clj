(ns ca.bccdc-phl.sequencing-runs.system
  (:require [clojure.string :as str]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as jdbc.connection]
            [clojure.pprint :refer [pprint]]
            [ca.bccdc-phl.sequencing-runs.config :as config]
            [ca.bccdc-phl.sequencing-runs.handlers :as handlers])
  (:import (com.zaxxer.hikari HikariDataSource)))

(def system-config
  {::config {}
   ::db {::config (ig/ref ::config)}
   ::server {::config (ig/ref ::config)
             ::db (ig/ref ::db)}})

(defmethod ig/init-key ::config [_ opts]
  (let [path (get opts :path)]
    (config/load-config path)))

(defmethod ig/init-key ::db [_ opts]
  (let [db-uri (get-in opts [::config :db :connection-uri])]
    (jdbc.connection/->pool com.zaxxer.hikari.HikariDataSource
                            {:jdbcUrl db-uri})))

(defmethod ig/init-key ::server [_ opts]
  (let [port (get-in opts [::config :server :port])
        db (get opts ::db)]
    (jetty/run-jetty (handlers/root-handler db) (-> opts
                                                    (dissoc :handler)
                                                    (assoc :join? false)
                                                    (assoc :port port)))))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

