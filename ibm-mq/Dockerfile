# Build stage
FROM amazoncorretto:17-alpine AS build
LABEL stage=build
COPY src      /root/src
COPY pom.xml  /root
COPY .mvn /bin/.mvn
COPY mvnw /bin
RUN mvnw -f ~/pom.xml -Dtest=!IbmMqApplicationIntegrationTests package

# Package stage
FROM amazoncorretto:17-alpine
COPY --from=build /root/target/ibm-mq.jar /opt/ibm-mq.jar
ENTRYPOINT ["java", "-jar", "/opt/ibm-mq.jar"]
