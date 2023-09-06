(ns ca.bccdc-phl.sequencing-runs.jsonapi)


(defn to-resource
  "Convert a map to a JSON API resource object."
  [entity type id-key]
  (let [id (get entity id-key)]
    {:type type
     :id id
     :attributes (dissoc entity id-key)}))

(defn to-resource-collection
  [entities type id-key]
  (map #(to-resource % type id-key) entities))

(defn create-link
  [rel href]
  {:rel rel
   :href href})

(defn add-links
  [resource links]
  (assoc resource :links links))
