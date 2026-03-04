# Stage 1 (builder): compile and package the application with Maven.
FROM amazoncorretto:21-alpine3.16 AS builder

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml ./
COPY hadiasaas-core/pom.xml hadiasaas-core/pom.xml
COPY hadiasaas-app/pom.xml hadiasaas-app/pom.xml
COPY hadiasaas-integration-tests/pom.xml hadiasaas-integration-tests/pom.xml

RUN mvn -B -q -DskipTests dependency:go-offline

COPY hadiasaas-core/src hadiasaas-core/src
COPY hadiasaas-app/src hadiasaas-app/src

RUN mvn -B -q -DskipTests -pl hadiasaas-app -am clean package

# Stage 2 (runtime): start from a clean image and copy only the executable JAR.
# We intentionally use FROM again for a multi-stage build to keep the final image smaller and safer. (No maven inside)
FROM amazoncorretto:21-alpine3.16

RUN apk add --no-cache tzdata wget

WORKDIR /opt/hadiasaas

COPY --from=builder /app/hadiasaas-app/target/*-exec.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/hadiasaas/app.jar"]
