FROM maven:3.9.15-eclipse-temurin-25-alpine AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -Dmaven.test.skip

FROM bellsoft/liberica-openjre-debian:25-cds AS layers
WORKDIR /application
COPY --from=builder /application/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM bellsoft/liberica-openjre-debian:25-cds

WORKDIR /application

COPY --from=layers /application/extracted/dependencies/ ./
COPY --from=layers /application/extracted/spring-boot-loader/ ./
COPY --from=layers /application/extracted/snapshot-dependencies/ ./
COPY --from=layers /application/extracted/application/ ./

RUN java -XX:AOTCacheOutput=app.aot -Dspring.profiles.active=training -Dspring.context.exit=onRefresh -jar app.jar

ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"
ENV AOT_CACHE="-XX:AOTCache=app.aot"
ENV AOT_LOG="-Xlog:aot"

ENTRYPOINT java \
    $JAVA_ERROR_FILE_OPTS \
    $AOT_CACHE \
    $AOT_LOG \
    -jar app.jar
