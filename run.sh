#!/bin/bash

echo "[INFO] --------- Se intenta desplegar servidor"
echo "[INFO] --------- logs en carpeta: ${LOGS_PATH}"

java -Xmx512m -Xms256m -jar servidor.jar >> $LOGS_PATH/server.log &


tail -f $LOGS_PATH/server.log
