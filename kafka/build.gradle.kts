plugins {
    java
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
}

group = "artiow.example"
version = "SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.cloud:spring-cloud-starter-zookeeper-discovery")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.brave:brave-instrumentation-kafka-clients")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks {
    bootJar {
        archiveVersion = ""
        exclude("application-*.yaml")
        exclude("dev")
    }
    jar {
        enabled = false
    }
    test {
        useJUnitPlatform()
    }
}
