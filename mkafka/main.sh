#!/bin/bash

kafka-console-consumer.sh --bootstrap-server kafka1:29091 --topic node --from-beginning