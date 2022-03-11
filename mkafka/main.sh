#!/bin/bash

kafka-console-consumer \
  --topic node220 \
  --bootstrap-server localhost:9092 \
  --from-beginning \
  --property print.key=true \
  --property key.separator="-"
  
  kafka-console-consumer.sh --bootstrap-server localhost:9091 --topic node322 --from-beginning