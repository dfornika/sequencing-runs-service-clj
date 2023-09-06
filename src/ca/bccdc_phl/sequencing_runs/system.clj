(ns ca.bccdc-phl.sequencing-runs.system
  (:require [clojure.string :as str]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as jdbc.connection]
            [com.brunobonacci.mulog :as u]
            [com.brunobonacci.mulog.utils :as mu-utils]
            [clojure.pprint :refer [pprint]]
            [ca.bccdc-phl.sequencing-runs.config :as config]
            [ca.bccdc-phl.sequencing-runs.handlers :as handlers])
  (:import (com.zaxxer.hikari HikariDataSource)))

(def system-config
  {::config {}
   ::console-logger {}
   ::db {::logger (ig/ref ::console-logger)
         ::config (ig/ref ::config)}
   ::handler {::db (ig/ref ::db)}
   ::server {::config (ig/ref ::config)
             ::logger (ig/ref ::console-logger)
             ::db (ig/ref ::db)
             ::handler (ig/ref ::handler)}})

(defmethod ig/init-key ::config [_ opts]
  (let [path (get opts :path)]
    (config/load-config path)))

(defmethod ig/init-key ::db [_ opts]
  (let [db-uri (get-in opts [::config :db :connection-uri])]
    (jdbc.connection/->pool com.zaxxer.hikari.HikariDataSource
                            {:jdbcUrl db-uri})))

(defmethod ig/halt-key! ::server [_ db]
  (.stop db))

(defmethod ig/init-key ::handler [_ opts]
  (let [db (get opts ::db)]
    (handlers/root-handler db)))

(defmethod ig/init-key ::server [_ opts]
  (let [port (get-in opts [::config :server :port])
        db (get opts ::db)
        handler (get opts ::handler)]
    (jetty/run-jetty handler (-> opts
                                 (dissoc :handler)
                                 (assoc :join? false)
                                 (assoc :port port)))))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(defmethod ig/init-key ::console-logger [_ opts]
  (let []
    (u/start-publisher! {:type :console-json
                         :transform (fn [events] (map #(update % :mulog/timestamp mu-utils/iso-datetime-from-millis) events))})))

