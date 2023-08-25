#!/bin/bash

flyway -configFiles=sequencing-runs-db/flyway-dev.conf migrate
