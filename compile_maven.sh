#!/bin/sh
# Ruta donde está instalado el SDK
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
# ac'a poner la ruta donde bajaron el ID
export MAVEN_HOME=/Users/jonnattangriffiths/herramientas/apache-maven-3.9.2
export PATH=$PATH:$MAVEN_HOME/bin

echo "--------- Limpia programa para ser compilado desde CERO"
mvn clean package
