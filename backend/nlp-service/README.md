# AssessAI NLP Service

Microservice de traitement du langage naturel (NLP) pour le système AssessAI - Système Automatique de Correction et d'Évaluation de Copies.

## Description

Ce service fournit des fonctionnalités d'analyse de texte pour l'évaluation automatique de copies d'examen. Il est intégré avec Eureka pour la découverte de services.

## Structure du Projet

```
assessai-nlp-service/
│
├── src/main/java/com/assessai/nlp/
│   ├── config/                # Configuration
│   ├── processor/             # Traitement NLP
│   ├── model/                 # Modèles et entités
│   ├── service/               # Services métier
│   ├── controller/            # API REST
│   └── NlpServiceApplication.java
│
├── src/main/resources/
│   ├── application.yml
│   └── nlp-models/           # Modèles NLP entraînés
│
├── docker/
│   └── Dockerfile
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Configuration

- **Port**: 8083
- **Service Name**: nlp-service
- **Eureka**: Configuré pour s'enregistrer automatiquement

## Endpoints API

### POST /api/nlp/analyze
Analyse un texte avec des options personnalisées.

**Request Body:**
```json
{
  "text": "Le texte à analyser",
  "language": "fr",
  "analysisTypes": ["grammar", "spelling", "coherence"]
}
```

**Response:**
```json
{
  "text": "Le texte à analyser",
  "score": 85.5,
  "analysis": {
    "wordCount": 5,
    "sentenceCount": 1,
    "characterCount": 20,
    "averageWordLength": 4.0,
    "language": "fr",
    "spellingErrors": 0,
    "grammarScore": 90.0,
    "coherenceScore": 85.0
  },
  "status": "SUCCESS",
  "message": "Analyse terminée avec succès"
}
```

### POST /api/nlp/analyze/simple
Analyse simple d'un texte.

**Request Body:**
```json
{
  "text": "Le texte à analyser"
}
```

### GET /api/nlp/health
Vérifie l'état de santé du service.

## Démarrage

### Localement

```bash
mvn spring-boot:run
```

### Avec Docker

```bash
docker-compose up -d
```

## Technologies

- Spring Boot 3.5.9
- Spring Cloud (Eureka Client)
- Java 21
- Maven

## Notes

- Le service s'enregistre automatiquement auprès d'Eureka
- Les modèles NLP peuvent être ajoutés dans `src/main/resources/nlp-models/`
- Le traitement NLP actuel est basique et peut être étendu avec des bibliothèques spécialisées

