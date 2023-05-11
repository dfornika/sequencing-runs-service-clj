(ns user
  (:require [ragtime.jdbc]
            [ragtime.repl]
            [integrant.repl]
            [integrant.core]
            [ca.bccdc-phl.sequencing-runs.config :as config]
            [ca.bccdc-phl.sequencing-runs.system :as system]))

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



(comment
  (ragtime.repl/migrate ragtime-config)
  (ragtime.repl/rollback ragtime-config)
  integrant.repl.state/system
  (go)
  (halt)
  (reset)
  (reset-all)
  )
