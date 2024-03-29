(ns ca.bccdc-phl.sequencing-runs.handlers
  (:require [reitit.ring]
            [muuntaja.core :as muuntaja]
            [reitit.coercion.spec]
            [reitit.ring.coercion]
            [reitit.ring.middleware.muuntaja]
            [reitit.ring.middleware.parameters]
            [reitit.ring.middleware.exception]
            [reitit.swagger]
            [ring.util.response :refer [response not-found]]
            [ring.middleware.json :refer [wrap-json-response]]
            [cheshire.core :as json]
            [com.brunobonacci.mulog :as u]
            [ca.bccdc-phl.sequencing-runs.crud :as crud]
            [ca.bccdc-phl.sequencing-runs.jsonapi :as jsonapi]
            [ca.bccdc-phl.sequencing-runs.middleware :refer [wrap-logging]]))


(defmulti get-instruments-illumina
  "Get a list of Illumina sequencing instruments."
  (fn [db request]
    (get-in request [:headers "accept"])))


(defmethod get-instruments-illumina "application/json"
  [db request]
  (cond
    (nil? (:instrument-id (:path-params request)))
    (let [instruments (crud/read db :sequencing_instrument_illumina)]
      (->> instruments
           (map #(update-keys % (comp keyword name)))
           (map #(dissoc % :pk))
           (response)))
    :else
    (let [instrument (first (crud/read db :sequencing_instrument_illumina :instrument_id (:instrument-id (:path-params request))))]
      (cond
        (nil? instrument)
        (-> (not-found "")
            (assoc-in [:headers "Content-Type"] "application/json;charset=utf-8"))
        :else (-> instrument
                  (update-keys (comp keyword name))
                  (dissoc :pk)
                  (response))))))


(defmethod get-instruments-illumina "application/vnd.api+json"
  [db request]
  (cond
    (nil? (:instrument-id (:path-params request)))
    (let [instruments (crud/read db :sequencing_instrument_illumina)]
      (->> instruments
           (map #(update-keys % (comp keyword name)))
           (map #(dissoc % :pk))
           (map #(jsonapi/to-resource % "sequencing-instrument" :instrument_id))
           (map #(jsonapi/add-links % (jsonapi/create-link "self" (str "/sequencing-instruments/illumina/" (:id %)))))
           (assoc {} :data)
           (response)))
    :else
    (let [instrument (first (crud/read db :sequencing_instrument_illumina :instrument_id (:instrument-id (:path-params request))))]
      (cond
        (nil? instrument)
        (-> (not-found "")
            (assoc-in [:headers "Content-Type"] "application/vnd.api+json;charset=utf-8"))
        :else (-> instrument
                  (update-keys (comp keyword name))
                  (dissoc :pk)
                  (jsonapi/to-resource "sequencing-instrument" :instrument_id)
                  (as-> resource (jsonapi/add-links resource [(jsonapi/create-link "self" (str "/sequencing-instruments/illumina/" (:id resource)))]))
                  (as-> data (assoc {} :data data))
                  (response))))))


(defmethod get-instruments-illumina "*/*"
  [db request]
  (get-instruments-illumina db (assoc-in request [:headers "accept"] "application/json")))


(defmulti create-instrument-illumina
  ""
  (fn [db request]
    (get-in request [:headers "content-type"])))


(defmethod create-instrument-illumina "application/json"
  [db request]
  (let [request-body (json/parse-string (slurp (:body request)) true)]
    (cond
      (nil? (:instrument_id request-body))
      {:status 400 :body nil}
      :else
      (do
        (->> request-body
             (crud/create! db :sequencing_instrument_illumina :instrument_id))
        (let [inserted-instrument (first (crud/read db :sequencing_instrument_illumina :instrument_id (:instrument_id request-body)))]
          (cond
            (nil? inserted-instrument)
            {:status 500 :body nil}
            :else
            {:status 201
             :headers {"Location" (str "/sequencing-instruments/illumina/" (:sequencing_instrument_illumina/instrument_id inserted-instrument))}
             :body (-> inserted-instrument
                       (update-keys (comp keyword name))
                       (dissoc :pk))}))))))


(defmethod create-instrument-illumina "application/vnd.api+json"
  [db request]
  (let [request-body (json/parse-string (slurp (:body request)) true)]
    (cond
      (nil? (get-in request-body [:data :attributes :instrument_id]))
      {:status 400 :body nil}
      :else
      (do
        (->> request-body
             (#(get-in % [:data :attributes]))
             (crud/create! db :sequencing_instrument_illumina :instrument_id))
        (let [inserted-instrument (first (crud/read db :sequencing_instrument_illumina :instrument_id (get-in request-body [:data :attributes :instrument_id])))]
          (cond
            (nil? inserted-instrument)
            {:status 500 :body nil}
            :else
            {:status 201
             :headers {"Location" (str "/sequencing-instruments/illumina/" (:sequencing_instrument_illumina/instrument_id inserted-instrument))}
             :body (-> inserted-instrument
                       (update-keys (comp keyword name))
                       (dissoc :pk)
                       (jsonapi/to-resource "sequencing-instrument" :instrument_id)
                       (as-> resource (jsonapi/add-links resource [(jsonapi/create-link "self" (str "/sequencing-instruments/illumina/" (:id resource)))]))
                       (as-> data (assoc {} :data data)))}))))))


(defn delete-instrument-illumina
  "Delete an Illumina sequencing instrument."
  [db request]
  (let [instrument-id (-> request :path-params :instrument-id)]
    (->> instrument-id
         (crud/delete! db :sequencing_instrument_illumina :instrument_id)
         (response))))


(defmulti get-instruments-nanopore
  "Get Nanopore sequencing instruments."
  (fn [db request]
    (get-in request [:headers "accept"])))


(defmethod get-instruments-nanopore "application/json"
  [db request]
  (cond
    (nil? (:instrument-id (:path-params request)))
    (let [instruments (crud/read db :sequencing_instrument_nanopore)]
      (->> instruments
           (map #(update-keys % (comp keyword name)))
           (map #(dissoc % :pk))
           (response)))
    :else
    (let [instrument (first (crud/read db :sequencing_instrument_nanopore :instrument_id (:instrument-id (:path-params request))))]
      (cond
        (nil? instrument)
        (-> (not-found "")
            (assoc-in [:headers "Content-Type"] "application/json;charset=utf-8"))
        :else (-> instrument
                  (update-keys (comp keyword name))
                  (dissoc :pk)
                  (response))))))


(defmethod get-instruments-nanopore "application/vnd.api+json"
  [db request]
  (cond
    (nil? (:instrument-id (:path-params request)))
    (let [instruments (crud/read db :sequencing_instrument_nanopore)]
      (->> instruments
           (map #(update-keys % (comp keyword name)))
           (map #(dissoc % :pk))
           (map #(jsonapi/to-resource % "sequencing-instrument" :instrument_id))
           (map #(jsonapi/add-links % (jsonapi/create-link "self" (str "/sequencing-instruments/nanopore/" (:id %)))))
           (assoc {} :data)
           (response)))
    :else
    (let [instrument (first (crud/read db :sequencing_instrument_nanopore :instrument_id (:instrument-id (:path-params request))))]
      (cond
        (nil? instrument)
        (-> (not-found "")
            (assoc-in [:headers "Content-Type"] "application/vnd.api+json;charset=utf-8"))
        :else (-> instrument
                  (update-keys (comp keyword name))
                  (dissoc :pk)
                  (jsonapi/to-resource "sequencing-instrument" :instrument_id)
                  (as-> resource (jsonapi/add-links resource [(jsonapi/create-link "self" (str "/sequencing-instruments/nanopore/" (:id resource)))]))
                  (as-> data (assoc {} :data data))
                  (response))))))


(defmethod get-instruments-nanopore "*/*"
  [db request]
  (get-instruments-nanopore db (assoc-in request [:headers "accept"] "application/json")))


(defn create-instrument-nanopore
  ""
  [db request]
  (let [request-body (json/parse-string (slurp (:body request)) true)] ;; TODO: Automatically convert request body to map using muuntaja?
    (->> request-body
         (crud/create! db :sequencing_instrument_nanopore :instrument_id)
         (response))))


(defn create-sequencing-run-illumina
  ""
  [db request]
  )


(defn make-routes
  ""
  [db]
  [["/openapi.json"
    {:get {:no-doc true
           :swagger {:info {:title "Sequencing Runs API"
                            :description "Information about sequencing runs."
                            :version "0.1.0-alpha"}
                     :basePath "/"}
           :handler (reitit.swagger/create-swagger-handler)}}]
   ["/sequencing-instruments/illumina"
    {:get {:handler (fn [request] (get-instruments-illumina db request))}
     :post {:handler (fn [request] (create-instrument-illumina db request))}}]
   ["/sequencing-instruments/illumina/:instrument-id"
    {:get {:handler (fn [request] (get-instruments-illumina db request))}
     :delete {:handler (fn [request] (delete-instrument-illumina db request))}}]
   ["/sequencing-instruments/nanopore"
    {:get {:handler (fn [request] (get-instruments-nanopore db request))}
     :post {:handler (fn [request] (create-instrument-nanopore db request))}}]])


(defn make-router
  ""
  [db]
  (reitit.ring/router
     (make-routes db)
     {:data {:coercion   reitit.coercion.spec/coercion
             :muuntaja   muuntaja/instance
             :middleware [#_reitit.ring.middleware.exception/exception-middleware
                          reitit.ring.middleware.parameters/parameters-middleware
                          reitit.ring.coercion/coerce-request-middleware
                          reitit.ring.coercion/coerce-response-middleware
                          reitit.ring.middleware.muuntaja/format-response-middleware]}}))

(defn root-handler
  ""
  [db]
  (wrap-json-response
   (reitit.ring/ring-handler
    (make-router db)
    (reitit.ring/redirect-trailing-slash-handler {:method :strip}))))
