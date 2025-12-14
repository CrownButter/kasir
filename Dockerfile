# 1. Build Stage
# Ganti dari 'alpine' ke versi standard (Ubuntu/Debian based)
# Versi Java TETAP 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Fix Windows line endings
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Run Stage
# Gunakan versi '25-jre' (tanpa akhiran -alpine)
# Ini tetap Java 25, tapi di atas Linux yang punya library font lengkap
FROM eclipse-temurin:25-jre
WORKDIR /app

# Install fontconfig & font standard agar PDF bisa dirender
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]