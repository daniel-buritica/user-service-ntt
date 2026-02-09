FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
COPY target/client-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8090
ENV PORT=8090
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]