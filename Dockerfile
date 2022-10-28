FROM maven:3.8.4-jdk-11 as compilador

RUN echo "Se crea carpeta de servidor" && \
    mkdir app

COPY . /app

RUN echo "Se compila la aplicación" && \
    cd /app && \
    mvn clean package && \
    cp -f server.app/target/*.jar ./servidor.jar

FROM centos:7

LABEL version=1.0.0
LABEL description="Jonnattan Griffiths"
LABEL product="Spring Boot Server App"

RUN useradd jonnattan && \
    mkdir -p app/logs/ && \
    yum -y update && \
    yum -y install java-11-openjdk


COPY --from=compilador /app/servidor.jar /app/servidor.jar
COPY application.yml /app/application.yml
COPY run.sh /app/run.sh

RUN chown -R jonnattan:jonnattan app/ && \
    chmod -R 755 app/

WORKDIR app/

ENV PORT 8089
ENV CONTEXT mobile
ENV LOGS_PATH ./logs
ENV BD_ADDR 192.168.0.15
ENV BD_PORT 3306
ENV BD_NAME emulator
ENV BD_USER emulator
ENV BD_PASS emulator

EXPOSE 8089

CMD [ "/bin/sh", "./run.sh" ]