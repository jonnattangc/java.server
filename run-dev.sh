#!/bin/bash

echo "[INFO] --------- Se intenta desplegar servidor"
mv server.app/target/*.jar server.jar


export BD_ADDR=api.jonnattan.cl
export BD_PORT=3306
export BD_NAME=emulator
export BD_USER=emulator
export BD_PASS=emulator
export PORT=8089
export CONTEXT=/emulator
export LOG_LEVEL=DEBUG
export AES_KEY=mAFaa23csdas5sdf12sght549u87y8adnjk


java -Xmx512m -Xms256m -jar server.jar 