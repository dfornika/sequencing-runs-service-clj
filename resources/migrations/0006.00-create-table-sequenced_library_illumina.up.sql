CREATE TABLE sequenced_library_illumina (
  id SERIAL PRIMARY KEY,
  library_id VARCHAR(255) NOT NULL,
  demultiplexing_id INTEGER,
  fastq_path_r1 VARCHAR(255),
  fastq_path_r2 VARCHAR(255),
  index VARCHAR(255),
  index2 VARCHAR(255),
  num_reads INTEGER,
  samplesheet_project_id VARCHAR(255),
  translated_project_id VARCHAR(255),
  CONSTRAINT fk_demultiplexing FOREIGN KEY(demultiplexing_id) REFERENCES sequencing_run_illumina_demultiplexing(id)
);
