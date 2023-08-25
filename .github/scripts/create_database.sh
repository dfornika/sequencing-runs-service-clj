#!/bin/bash

PGPASSWORD=postgres psql -U postgres -h localhost -f sequencing-runs-db/.github/scripts/create_database.pgsql
