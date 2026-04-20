FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY . .

EXPOSE 12345

CMD ["java", "-cp", "chat-server.jar:lib/sqlite-jdbc-3.51.3.0.jar", "server.Serveur"]