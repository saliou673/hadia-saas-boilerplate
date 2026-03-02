# Stage 1 (builder): compile and package the application with Maven.
FROM amazoncorretto:21-alpine3.16 AS builder

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml ./
COPY maitrisetcf-core/pom.xml maitrisetcf-core/pom.xml
COPY maitrisetcf-app/pom.xml maitrisetcf-app/pom.xml
COPY maitrisetcf-integration-tests/pom.xml maitrisetcf-integration-tests/pom.xml

RUN mvn -B -q -DskipTests dependency:go-offline

COPY maitrisetcf-core/src maitrisetcf-core/src
COPY maitrisetcf-app/src maitrisetcf-app/src

RUN mvn -B -q -DskipTests -pl maitrisetcf-app -am clean package

# Stage 2 (runtime): start from a clean image and copy only the executable JAR.
# We intentionally use FROM again for a multi-stage build to keep the final image smaller and safer. (No maven inside)
FROM amazoncorretto:21-alpine3.16

RUN apk add --no-cache tzdata wget

WORKDIR /opt/maitrisetcf

COPY --from=builder /app/maitrisetcf-app/target/*-exec.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/maitrisetcf/app.jar"]
