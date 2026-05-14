FROM openjdk:11-ea-slim
WORKDIR /app
COPY target/myShop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]