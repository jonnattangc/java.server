#!/bin/sh
# Ruta donde está instalado el SDK
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.3.1.jdk/Contents/Home
# ac'a poner la ruta donde bajaron el ID
export MAVEN_HOME=/Users/jonnattangriffiths/herramientas/apache-maven-3.9.6
export PATH=$PATH:$MAVEN_HOME/bin

echo "--------- Limpia programa para ser compilado desde CERO"
mvn -e clean package
