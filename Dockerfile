FROM maven:3.9-eclipse-temurin-17 as compilador

RUN echo "Se crea carpeta de servidor" && \
    mkdir app

COPY . /app

RUN echo "Se compila la aplicación" && \
    cd /app && \
    mvn clean package -DskipTests && \
    cp -f server.app/target/*.jar ./servidor.jar

FROM eclipse-temurin:17-jre

LABEL version=1.0.0
LABEL description="Jonnattan Griffiths"
LABEL product="Spring Boot Server App"

RUN useradd jonnattan && \
    mkdir -p /app/logs/ 

COPY --from=compilador /app/servidor.jar /app/servidor.jar

RUN chown -R jonnattan:jonnattan /app && \
    chmod -R 755 /app

WORKDIR /app

USER jonnattan

ENV PORT 8089
ENV CONTEXT /emulator
ENV LOG_LEVEL debug
ENV BD_ADDR ''
ENV BD_PORT '3306'
ENV BD_NAME ''
ENV BD_USER ''
ENV BD_PASS ''
ENV ENV dev

EXPOSE 8089

# CMD [ "/bin/sh", "./run.sh" ]
CMD [ "java", "-Xmx512m", "-Xms256m", "-jar", "servidor.jar" ]