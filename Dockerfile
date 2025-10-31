FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY . .

# Fix CRLF and add execute bit so the Maven Wrapper can run
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Build the jar
RUN ./mvnw -q -DskipTests package

# Render provides $PORT; pass it to Spring
ENV PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT} -jar target/*.jar"]
