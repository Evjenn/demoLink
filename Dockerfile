FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/src
WORKDIR /home/src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
COPY --from=build /home/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]