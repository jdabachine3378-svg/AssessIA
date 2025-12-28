FROM openjdk:21-jdk-slim

WORKDIR /app

# Copier le fichier JAR
COPY target/assessai-scoring-service-*.jar app.jar

# Exposer le port
EXPOSE 8082

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

