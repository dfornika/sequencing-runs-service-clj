#!/usr/bin/bash 

java -Xss256k -Xmx512m -XX:MaxRAM=100m -XX:+UseSerialGC -jar target/sequencing-runs-service-0.1.0-standalone.jar -c dev-config.edn
