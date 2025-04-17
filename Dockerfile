FROM maven:3.9.9 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -Dmaven.test.skip

FROM amazoncorretto:21-alpine AS layers
WORKDIR /application
COPY --from=builder /application/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM amazoncorretto:21-alpine
VOLUME /tmp

WORKDIR /application

COPY --from=layers /application/extracted/dependencies/ ./
COPY --from=layers /application/extracted/spring-boot-loader/ ./
COPY --from=layers /application/extracted/snapshot-dependencies/ ./
COPY --from=layers /application/extracted/application/ ./

RUN java -XX:ArchiveClassesAtExit=app.jsa -Dspring.context.exit=onRefresh -jar app.jar & exit 0

ENV JAVA_CDS_OPTS="-XX:SharedArchiveFile=app.jsa -Xlog:class+load:file=/tmp/classload.log"
ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"

ENTRYPOINT java \
    $JAVA_ERROR_FILE_OPTS \
    $JAVA_CDS_OPTS \
    -jar app.jar
