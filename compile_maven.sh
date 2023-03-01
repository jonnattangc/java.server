#!/bin/sh
# Ruta donde está instalado el SDK
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_261.jdk/Contents/Home
# ac'a poner la ruta donde bajaron el ID
export MAVEN_HOME=/Users/jonnattangriffiths/herramientas/apache-maven-3.8.6
export PATH=$PATH:$MAVEN_HOME/bin

echo "--------- Limpia programa para ser compilado desde CERO"
mvn clean package

