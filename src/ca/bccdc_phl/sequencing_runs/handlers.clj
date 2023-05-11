(ns ca.bccdc-phl.sequencing-runs.handlers
  (:require [reitit.ring]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ca.bccdc-phl.sequencing-runs.crud :as crud]))


(defn get-instruments-illumina
  ""
  [db request]
  (let [entities (crud/read db :sequencing_instrument_illumina)]
    (response entities)))
  

(defn root-handler
  ""
  [db]
  (wrap-json-response
   (reitit.ring/ring-handler
    (reitit.ring/router
     [["/instruments/illumina" {:get (fn [request] (get-instruments-illumina db request))}]]
     ))))
