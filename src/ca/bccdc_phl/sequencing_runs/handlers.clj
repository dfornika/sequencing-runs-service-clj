(ns ca.bccdc-phl.sequencing-runs.handlers
  (:require [reitit.ring]
            [muuntaja.core :as muuntaja]
            [reitit.coercion.spec]
            [reitit.ring.coercion]
            [reitit.ring.middleware.muuntaja]
            [reitit.ring.middleware.parameters]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [cheshire.core :as json]
            [taoensso.timbre :as timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]
            [ca.bccdc-phl.sequencing-runs.crud :as crud]))


(defn get-instruments-illumina
  ""
  [db request]
  (let [instruments (crud/read db :sequencing_instrument_illumina)]
    (->> instruments
         (map #(update-keys % (comp keyword name)))
         (map #(dissoc % :pk))
         (response))))


(defn create-instrument-illumina
  ""
  [db request]
  (let [request-body (json/parse-string (slurp (:body request)) true)
        response-body (->> request-body
                           (crud/create! db :sequencing_instrument_illumina :instrument_id))]
    (cond
      (nil? response-body) {:status 409 :body nil}
      :else (response {:status 201 :body response-body}))))


(defn get-instruments-nanopore
  ""
  [db request]
  (let [instruments (crud/read db :sequencing_instrument_nanopore)]
    (->> instruments
         (map #(update-keys % (comp keyword name)))
         (map #(dissoc % :pk))
         (response))))


(defn create-instrument-nanopore
  ""
  [db request]
  (info request)
  (let [request-body (json/parse-string (slurp (:body request)) true)] ;; TODO: Automatically convert request body to map using muuntaja?
    (->> request-body
         (crud/create! db :sequencing_instrument_nanopore :instrument_id)
         (response))))


(defn create-sequencing-run-illumina
  ""
  [db request]
  )


(defn root-handler
  ""
  [db]
  (wrap-json-response
   (reitit.ring/ring-handler
    (reitit.ring/router
     [["/sequencing-instruments/illumina" {:get {:handler (fn [request] (get-instruments-illumina db request))}
                                           :post {:handler (fn [request] (create-instrument-illumina db request))}}]
      ["/sequencing-instruments/nanopore" {:get {:handler (fn [request] (get-instruments-nanopore db request))}
                                           :post {:handler (fn [request] (create-instrument-nanopore db request))}}]]
     {:data {:coercion   reitit.coercion.spec/coercion
             :muuntaja   muuntaja/instance
             :middleware [reitit.ring.middleware.parameters/parameters-middleware
                          reitit.ring.coercion/coerce-request-middleware
                          reitit.ring.coercion/coerce-response-middleware
                          reitit.ring.middleware.muuntaja/format-response-middleware]}})
    (reitit.ring/redirect-trailing-slash-handler))))
