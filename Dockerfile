# 1. Build Stage
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
# Menggunakan versi standard (bukan alpine) agar support Font & Graphics lancar
FROM eclipse-temurin:25-jre
WORKDIR /app

# (Opsional) Install fontconfig jika di image ini belum ada,
# tapi biasanya di image non-alpine sudah aman atau lebih mudah diinstall.
RUN apt-get update && apt-get install -y fontconfig libfreetype6 && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]