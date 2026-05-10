FROM maven:3.9.14 AS build
COPY . /app
WORKDIR /app

RUN mvn clean package -DskipTests
FROM eclipse-temurin:25-jre
COPY --from=build /app/target/ChatApp-1.0-SNAPSHOT.jar ./app.jar
EXPOSE 12345
ENTRYPOINT ["java", "-cp", "app.jar", "ds.agh.chatapp.server.Launcher"]
CMD ["12345"]