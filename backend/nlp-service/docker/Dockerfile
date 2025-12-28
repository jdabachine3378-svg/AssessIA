FROM openjdk:21-jdk-slim

WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copier le code source
COPY src ./src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8083

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "target/nlp-service-0.0.1-SNAPSHOT.jar"]

