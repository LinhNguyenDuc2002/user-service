# Stage 1: Build ứng dụng với Maven và JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

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
