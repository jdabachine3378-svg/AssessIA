# AssessAI - Scoring Service

Microservice de scoring et d'évaluation automatique de copies pour le système AssessAI.

## Description

Le microservice Scoring est responsable du calcul et de la persistance des notes. Il offre :
- Scoring automatique basé sur différents algorithmes
- Intégration avec RabbitMQ pour le traitement asynchrone
- Cache Redis pour améliorer les performances
- API REST pour la gestion des scores
- Enregistrement dans Eureka pour la découverte de services

## Structure du Projet

```
assessai-scoring-service/
│
├── src/main/java/com/assessai/scoring/
│   ├── config/                # Configuration (Redis, RabbitMQ)
│   ├── consumer/              # Consommateurs des queues
│   ├── processor/             # Algorithmes de scoring
│   ├── model/                  # Modèles de données
│   ├── repository/             # Repositories Spring Data
│   ├── service/                # Services métier
│   ├── controller/             # API REST
│   └── ScoringServiceApplication.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── scoring-rules/          # Règles de notation
│   └── grading-templates/      # Templates de correction
│
├── docker/
│   └── Dockerfile
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Technologies Utilisées

- **Spring Boot 3.5.9**
- **Spring Cloud** (Eureka Client)
- **Spring Data JPA** (MySQL)
- **Spring Data Redis**
- **RabbitMQ** (Message Broker)
- **MySQL 8.0**
- **Redis 7**
- **Java 21**

## Prérequis

- Java 21
- Maven 3.8+
- MySQL 8.0
- Redis 7
- RabbitMQ 3
- Eureka Server (pour la découverte de services)

## Configuration

### Base de données MySQL

Le service utilise la base de données `assessiabase`. Assurez-vous que la base de données existe dans phpMyAdmin.

### Configuration application.yml

Modifiez `src/main/resources/application.yml` selon votre environnement :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/assessiabase
    username: votre_username
    password: votre_password
```

## Démarrage Local

### 1. Démarrer les services dépendants

```bash
docker-compose up -d mysql redis rabbitmq
```

### 2. Compiler le projet

```bash
mvn clean package
```

### 3. Démarrer l'application

```bash
java -jar target/assessai-scoring-service-0.0.1-SNAPSHOT.jar
```

Ou avec Maven :

```bash
mvn spring-boot:run
```

## Démarrage avec Docker

```bash
docker-compose up -d
```

## API REST

### Endpoints Principaux

#### Créer un score
```http
POST /api/scoring/score
Content-Type: application/json

{
  "copyId": 1,
  "examId": 1,
  "studentId": 1,
  "content": "Contenu de la copie...",
  "scoringAlgorithm": "AUTOMATIC"
}
```

#### Obtenir un score par ID
```http
GET /api/scoring/score/{id}
```

#### Obtenir les scores d'un examen
```http
GET /api/scoring/scores/exam/{examId}
```

#### Obtenir les scores d'un étudiant
```http
GET /api/scoring/scores/student/{studentId}
```

#### Statistiques d'un examen
```http
GET /api/scoring/exam/{examId}/statistics
```

#### Health Check
```http
GET /api/scoring/health
```

## Algorithmes de Scoring

### 1. AUTOMATIC
Scoring automatique basé sur :
- Longueur du texte
- Structure et organisation
- Ponctuation
- Diversité lexicale

### 2. KEYWORD_BASED
Scoring basé sur la présence de mots-clés spécifiques.

## Queues RabbitMQ

- **scoring.queue** : Queue pour recevoir les demandes de scoring
- **scoring.result.queue** : Queue pour publier les résultats

## Cache Redis

Le service utilise Redis pour mettre en cache :
- Les scores par examen
- Les règles de scoring actives

## Port

Le service s'exécute sur le port **8082** par défaut.

## Eureka

Le service s'enregistre automatiquement dans Eureka avec le nom : `assessai-scoring-service`

## Développement

### Ajouter un nouvel algorithme de scoring

1. Implémenter l'interface `ScoringProcessor`
2. Annoter avec `@Component`
3. Le processeur sera automatiquement découvert par `ScoringProcessorFactory`

## Licence

Propriétaire - AssessAI

