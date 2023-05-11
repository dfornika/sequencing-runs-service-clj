CREATE TABLE sequencing_instrument_nanopore (
  id SERIAL PRIMARY KEY,
  instrument_id VARCHAR(255) NOT NULL,
  type VARCHAR(255),
  model VARCHAR(255),
  status VARCHAR(255),
  timestamp_status_updated TIMESTAMP
);
