FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package
CMD ["java", "-Dserver.port=$PORT", "-jar", "target/*.jar"]
