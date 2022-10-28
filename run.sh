#!/bin/bash

echo "[INFO] --------- Se intenta desplegar servidor"
echo "[INFO] --------- logs en $LOGS_PATH"

java -Xmx512m -Xms256m -jar servidor.jar >> $LOGS_PATH/mobile_server.log &

tail -f $LOGS_PATH/mobile_server.log
