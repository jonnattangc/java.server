#!/bin/sh

# Ruta donde est'a instalado el SDK
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk.jdk/Contents/Home
# ac'a poner la ruta donde bajaron el ID
#export MAVEN_HOME=/home/jonnattan/utils/apache-maven-3.8.6
export MAVEN_HOME=/Users/jonnattangriffiths/tools/apache-maven-3.9.14
export PATH=$PATH:$MAVEN_HOME/bin

export ENV=local
export BD_ADDR=dev.jonnattan.com
export BD_PORT=3306
export BD_NAME=emulator
export BD_USER=emulator
export BD_PASS=emulator
export PORT=8089
export CONTEXT=/emulator
export LOG_LEVEL=DEBUG
export AES_KEY=mAFaa23csdas5sdf12sght549u87y8adnjk


# Define un nombre al war que se genera
export JAR_NAME=server.jar
export JAR_COMPILED=server.app-1.2.0-SNAPSHOT.jar

rm -f $JAR_NAME

echo "[INFO] Limpia programa para ser compilado desde CERO"
mvn -e clean package

if [ -e server.app/target/$JAR_COMPILED ]
then
  clear
  mv server.app/target/$JAR_COMPILED $JAR_NAME
  echo "[INFO] El programa $JAR_NAME ha sido compilado exitosamente"
  echo " \n\n\n"
  java -Xmx1024m -Xms512m -jar $JAR_NAME
fi
