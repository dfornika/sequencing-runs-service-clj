(ns ca.bccdc-phl.sequencing-runs.middleware
  (:require [com.brunobonacci.mulog :as u]
            [com.brunobonacci.mulog.utils :as mu-utils]))

(defn wrap-logging
  ""
  [handler]
  (fn [request]
    (let [request-id (or (:request-id (:headers request)) (mu-utils/random-uid))]
      (-> request
          (assoc :request-id request-id)
          (assoc :mulog/request-id request-id)
          (assoc :mulog/level :info)
          (assoc :mulog/message (str (:request-method request) " " (:uri request)))
          (assoc :mulog/extra {:request-method (:request-method request)
                               :uri (:uri request)
                               :query-params (:query-params request)
                               :path-params (:path-params request)
                               :headers (:headers request)
                               :body (:body request)})
          (u/log ::request :info)
          (handler)))))
