# 1. Build Stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Fix Windows line endings
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# === [BAGIAN INI YANG DITAMBAHKAN] ===
# Install library font agar fitur PDF berjalan di Alpine Linux
RUN apk add --no-cache fontconfig ttf-dejavu freetype
# =====================================

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]