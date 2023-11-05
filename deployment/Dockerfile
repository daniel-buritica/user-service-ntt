FROM eclipse-temurin:17.0.5_8-jdk-alpine
WORKDIR /opt/app
COPY ../application/target/application-1.0-SNAPSHOT.jar app.jar
EXPOSE $PORT
ENTRYPOINT ["java","-jar","app.jar"]