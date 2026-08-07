# Stage 1: Build ứng dụng với Maven và JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy JAR vào container và cài vào Maven local
COPY src/main/resources/lib/service-foundation-0.0.1-SNAPSHOT.jar /tmp/
RUN mvn install:install-file \
    -Dfile=/tmp/service-foundation-0.0.1-SNAPSHOT.jar \
    -DgroupId=com.example \
    -DartifactId=service-foundation \
    -Dversion=0.0.1-SNAPSHOT \
    -Dpackaging=jar

COPY . .

# Build ứng dụng Spring Boot
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng với JRE 21 (Nhẹ hơn JDK)
# Dùng JRE thay vì JDK để image chạy thật nhẹ nhất có thể
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
