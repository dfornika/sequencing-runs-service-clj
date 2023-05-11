(ns ca.bccdc-phl.sequencing-runs.handlers
  (:require [reitit.ring]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [taoensso.timbre :as timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]
            [ca.bccdc-phl.sequencing-runs.crud :as crud]))


(defn get-instruments-illumina
  ""
  [db request]
  (let [instruments (crud/read db :sequencing_instrument_illumina)]
    (response instruments)))


(defn create-instrument-illumina
  ""
  [db request]
  (info request)
  (-> request
      (dissoc :reitit.core/match)
      (dissoc :reitit.core/router)
      (update :body slurp)
      (response)))

(defn root-handler
  ""
  [db]
  (wrap-json-response
   (reitit.ring/ring-handler
    (reitit.ring/router
     [["/instruments/illumina" {:get {:handler (fn [request] (get-instruments-illumina db request))}
                                :post {:handler (fn [request] (create-instrument-illumina db request))}}]]
     ))))
