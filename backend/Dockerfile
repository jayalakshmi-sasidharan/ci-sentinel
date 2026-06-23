# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update || true && \
    apk add --no-cache maven 2>/dev/null || \
    (wget -q https://archive.apache.org/dist/maven/maven-3/3.9.1/binaries/apache-maven-3.9.1-bin.tar.gz && \
    tar -xzf apache-maven-3.9.1-bin.tar.gz && \
    mv apache-maven-3.9.1 /opt/maven)

ENV PATH="/opt/maven/bin:${PATH}"

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]