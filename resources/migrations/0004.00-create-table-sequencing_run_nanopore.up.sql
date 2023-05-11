CREATE TABLE sequencing_run_nanopore (
  id SERIAL PRIMARY KEY,
  sequencing_run_id VARCHAR(255) NOT NULL,
  instrument_id VARCHAR(255),
  flowcell_id VARCHAR(255),
  flowcell_product_code VARCHAR(255),
  num_reads_passed_filter INTEGER,
  num_reads_total INTEGER,
  protocol_id VARCHAR(255),
  run_date DATE,
  sample_sheet_path VARCHAR(255),
  timestamp_protocol_run_started TIMESTAMP,
  timestamp_protocol_run_ended TIMESTAMP,
  yield_gigabases FLOAT
);
