#!/bin/sh
# Ruta donde está instalado el SDK
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
# ac'a poner la ruta donde bajaron el ID
export MAVEN_HOME=/home/jonnattan/utils/apache-maven-3.8.6
export PATH=$PATH:$MAVEN_HOME/bin

echo "--------- Limpia programa para ser compilado desde CERO"
mvn clean package
