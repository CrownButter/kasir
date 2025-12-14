# 1. Build Stage
# Menggunakan JDK 25 (Standard Version - bukan Alpine)
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
# Menggunakan JRE 25 (Standard Version - bukan Alpine)
# Image ini memiliki library Font & Grafik lengkap (fontconfig, libfreetype, dll)
FROM eclipse-temurin:25-jre
WORKDIR /app

# Install font tambahan untuk memastikan PDFBox berjalan lancar
RUN apt-get update && apt-get install -y \
    fontconfig \
    libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]