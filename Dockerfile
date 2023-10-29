FROM openjdk:17-alpine
WORKDIR /app
COPY ./ ./

RUN chmod +x /app/docker-entrypoint.sh
RUN chmod +x /app/gradlew
RUN ./gradlew build -x test