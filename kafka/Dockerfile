# Build stage
FROM amazoncorretto:17-alpine AS build-image
RUN apk add --no-cache binutils

COPY gradle   /bin/gradle
COPY gradlew  /bin
COPY thinify  /bin
RUN gradlew -v

COPY src                  /src
COPY build.gradle.kts     /
COPY settings.gradle.kts  /
RUN gradlew assemble
RUN thinify /build/libs/kafka.jar 17 /build/runtime

# Package stage
FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ARG APPUSER=appuser
RUN adduser --no-create-home -u 1000 -D APPUSER

COPY --from=build-image /build/runtime $JAVA_HOME
COPY --from=build-image --chown=1000:1000 /build/libs/kafka.jar /opt/kafka.jar
USER 1000
ENTRYPOINT ["java", "-jar", "/opt/kafka.jar"]
