CREATE TABLE sequencing_run_illumina_demultiplexing (
  id SERIAL PRIMARY KEY,
  sequencing_run_id INTEGER,
  demultiplexing_id VARCHAR(255) NOT NULL,
  samplesheet_path VARCHAR(255),
  CONSTRAINT fk_sequencing_run FOREIGN KEY(sequencing_run_id) REFERENCES sequencing_run_illumina(id)
);
