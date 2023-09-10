# Build stage
FROM amazoncorretto:17-alpine AS build
LABEL stage=build
COPY src                  /src
COPY build.gradle.kts     /
COPY settings.gradle.kts  /
COPY gradle   /bin/gradle
COPY gradlew  /bin
RUN gradlew assemble

# Package stage
FROM amazoncorretto:17-alpine
COPY --from=build /build/libs/kafka.jar /opt/kafka.jar
ENTRYPOINT ["java", "-jar", "/opt/kafka.jar"]
