# =========================
# Stage 1 -> Build
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# =========================
# Stage 2 -> Runtime
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/CompactURL.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]