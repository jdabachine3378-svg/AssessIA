# 🎓 AssessAI - Plateforme d'Évaluation Intelligente

<div align="center">

![AssessAI](https://img.shields.io/badge/AssessAI-v1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![React](https://img.shields.io/badge/React-18.2.0-61DAFB)
![Java](https://img.shields.io/badge/Java-21-orange)

**Une plateforme d'évaluation automatisée basée sur l'intelligence artificielle pour scanner, analyser et noter des copies d'examens.**

[🚀 Démarrage Rapide](#-installation) • [📖 Documentation](#-documentation) • [🏗️ Architecture](#-architecture) • [🤝 Contribution](#-contribution)

</div>

---

## 📋 Table des Matières

- [✨ Fonctionnalités](#-fonctionnalités)
- [🏗️ Architecture](#-architecture)
- [🛠️ Technologies](#️-technologies)
- [📦 Prérequis](#-prérequis)
- [🚀 Installation](#-installation)
- [🔧 Configuration](#-configuration)
- [📚 Documentation API](#-documentation-api)
- [🎨 Captures d'écran](#-captures-décran)
- [🤝 Contribution](#-contribution)
- [📄 Licence](#-licence)

---

## ✨ Fonctionnalités

### 👨‍🏫 Pour les Enseignants
- 📄 **Scan de copies PDF** - Upload et traitement automatique des copies
- 📝 **Définition de référence** - Création de modèles de correction
- 🤖 **Évaluation automatique** - Notation intelligente basée sur l'IA
- 💬 **Feedback personnalisé** - Génération de commentaires adaptatifs
- 📊 **Tableau de bord** - Visualisation des résultats en temps réel
- 📈 **Export CSV** - Exportation des rapports d'évaluation

### 🎓 Pour les Étudiants
- 🔍 **Consultation de résultats** - Accès sécurisé aux notes
- 📋 **Feedback détaillé** - Commentaires personnalisés sur les performances
- 📅 **Historique** - Suivi des évaluations passées

### 🔐 Sécurité
- 🔑 **Authentification JWT** - Système de tokens sécurisé
- 👤 **Gestion des rôles** - Séparation Enseignant/Étudiant
- 🛡️ **CORS configuré** - Protection contre les attaques XSS

---

## 🏗️ Architecture

AssessAI utilise une **architecture microservices** avec Spring Cloud :

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (React)                        │
│                     http://localhost:3000                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              API GATEWAY (Spring Cloud Gateway)              │
│                  http://localhost:8081                       │
│  - Routing                                                   │
│  - Authentication Filter                                     │
│  - Load Balancing                                           │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Eureka Server│ │  Services    │ │   RabbitMQ   │
│   :8761      │ │              │ │   :5672      │
└──────────────┘ └──────────────┘ └──────────────┘
                        │
        ┌───────────────┼───────────────┬───────────────┐
        ▼               ▼               ▼               ▼
  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
  │   OCR    │   │   NLP    │   │ Scoring  │   │ Feedback │
  │  :8082   │   │  :8083   │   │  :8084   │   │  :8085   │
  │          │   │          │   │          │   │          │
  │Tesseract │   │ Analysis │   │Evaluation│   │ Comments │
  └──────────┘   └──────────┘   └──────────┘   └──────────┘
```

### Microservices

| Service | Port | Description |
|---------|------|-------------|
| **Eureka Server** | 8761 | Service Discovery & Registry |
| **API Gateway** | 8081 | Point d'entrée unique, routing, auth |
| **OCR Service** | 8082 | Extraction de texte PDF (Tesseract) |
| **NLP Service** | 8083 | Analyse et nettoyage de texte |
| **Scoring Service** | 8084 | Évaluation et notation automatique |
| **Feedback Service** | 8085 | Génération de feedback personnalisé |

---

## 🛠️ Technologies

### Backend
- **Java 21** - Langage principal
- **Spring Boot 3.4.1** - Framework applicatif
- **Spring Cloud** - Architecture microservices
  - Netflix Eureka - Service discovery
  - Spring Cloud Gateway - API Gateway
  - Spring Cloud LoadBalancer - Load balancing
- **RabbitMQ** - Message broker
- **MySQL** - Base de données
- **Tesseract OCR** - Reconnaissance optique de caractères
- **JWT** - Authentification
- **Maven** - Gestion des dépendances

### Frontend
- **React 18.2.0** - Bibliothèque UI
- **Vite 5.0.8** - Build tool moderne
- **TailwindCSS 3.3.6** - Framework CSS utility-first
- **Axios 1.6.2** - Client HTTP
- **React Router 6.20.0** - Navigation SPA

---

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☕ **Java 21** ([Télécharger](https://www.oracle.com/java/technologies/downloads/#java21))
- 📦 **Maven 3.8+** ([Télécharger](https://maven.apache.org/download.cgi))
- 🐰 **RabbitMQ** ([Télécharger](https://www.rabbitmq.com/download.html))
- 🗄️ **MySQL 8.0+** ([Télécharger](https://dev.mysql.com/downloads/mysql/))
- 🔧 **Node.js 18+** et **npm** ([Télécharger](https://nodejs.org/))
- 📄 **Tesseract OCR** ([Installation](https://github.com/tesseract-ocr/tesseract))

### Installation Tesseract

**macOS:**
```bash
brew install tesseract
brew install tesseract-lang  # Pour le français
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install tesseract-ocr tesseract-ocr-fra
```

**Windows:**
Téléchargez l'installateur depuis [GitHub](https://github.com/UB-Mannheim/tesseract/wiki)

---

## 🚀 Installation

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/jdabachine3378-svg/AssessIA.git
cd assessai
```

### 2️⃣ Configuration de la base de données

```sql
-- Créer les bases de données
CREATE DATABASE assessai_feedback;
CREATE DATABASE assessai_scoring;

-- Créer un utilisateur
CREATE USER 'assessai'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON assessai_*.* TO 'assessai'@'localhost';
FLUSH PRIVILEGES;
```

### 3️⃣ Démarrer RabbitMQ

```bash
# macOS
brew services start rabbitmq

# Linux
sudo systemctl start rabbitmq-server

# Accéder à l'interface admin: http://localhost:15672
# Credentials par défaut: guest/guest
```

### 4️⃣ Lancer le Backend

```bash
cd backend

# Méthode 1 : Script automatique
./start-services.sh

# Méthode 2 : Démarrage manuel
# Terminal 1 - Eureka Server
cd eureka-server
mvn spring-boot:run

# Terminal 2 - Gateway
cd gateway
mvn spring-boot:run

# Terminal 3 - OCR Service
cd ocr-service
mvn spring-boot:run

# Terminal 4 - NLP Service
cd nlp-service
mvn spring-boot:run

# Terminal 5 - Scoring Service
cd scoring-service
mvn spring-boot:run

# Terminal 6 - Feedback Service
cd feedback-service
mvn spring-boot:run
```

### 5️⃣ Lancer le Frontend

```bash
cd frontend

# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm run dev

# Le frontend sera accessible sur http://localhost:3000
```

---

## 🔧 Configuration

### Backend Configuration

Les fichiers de configuration se trouvent dans `src/main/resources/application.yaml` de chaque service.

**Gateway (`gateway/src/main/resources/application.yaml`):**
```yaml
server:
  port: 8081

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: ocr-service
          uri: lb://ocr-service
          predicates:
            - Path=/ocr/**
```

**Variables d'environnement:**
```bash
# JWT Secret Key
JWT_SECRET=assessai-secret-key-for-jwt-token-generation-minimum-256-bits

# Database
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=assessai
MYSQL_PASSWORD=password

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
```

### Frontend Configuration

**Vite Proxy (`frontend/vite.config.js`):**
```javascript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

---

## 📚 Documentation API

### Authentication

**Endpoints publics** (pas d'authentification requise):
- `/ocr/**`
- `/nlp/**`
- `/scoring/**`
- `/feedback/**`

### OCR Service

**POST** `/ocr/process`
- **Description:** Extrait le texte d'un fichier PDF
- **Content-Type:** `multipart/form-data`
- **Body:** 
  ```
  file: [PDF File]
  ```
- **Response:**
  ```json
  {
    "extractedText": "Le texte extrait du PDF..."
  }
  ```

### NLP Service

**POST** `/nlp/analyze`
- **Description:** Analyse et nettoie le texte
- **Body:**
  ```json
  {
    "text": "Texte à analyser..."
  }
  ```
- **Response:**
  ```json
  {
    "cleanedText": "Texte nettoyé",
    "keywords": ["mot1", "mot2"],
    "sentiment": "neutral"
  }
  ```

### Scoring Service

**POST** `/scoring/evaluate`
- **Description:** Évalue et note une réponse
- **Body:**
  ```json
  {
    "studentText": "Réponse de l'étudiant",
    "referenceText": "Réponse de référence"
  }
  ```
- **Response:**
  ```json
  {
    "score": 15,
    "missingPoints": ["Complétude", "Précision"]
  }
  ```

### Feedback Service

**POST** `/feedback/generate`
- **Description:** Génère un feedback personnalisé
- **Body:**
  ```json
  {
    "studentId": "STU123",
    "score": 15,
    "missingPoints": ["Précision"],
    "studentText": "...",
    "referenceText": "..."
  }
  ```
- **Response:**
  ```json
  {
    "feedback": "Bon travail ! Vous avez une bonne compréhension..."
  }
  ```

---

## 🎨 Captures d'écran

### Page de Connexion
Interface moderne avec gradient et glassmorphism

### Dashboard Enseignant
- Gestion des références
- Scanner de copies
- Tableau de résultats

### Portail Étudiant
- Consultation de résultats
- Feedback détaillé

---

## 🧪 Tests

### Backend
```bash
# Exécuter tous les tests
cd backend
mvn test

# Tests d'un service spécifique
cd ocr-service
mvn test
```

### Frontend
```bash
cd frontend
npm run test
```

---

## 📊 Monitoring

### Eureka Dashboard
Accédez au tableau de bord Eureka : http://localhost:8761

### RabbitMQ Management
Interface d'administration : http://localhost:15672

### Health Checks
Chaque service expose un endpoint health :
- Gateway: http://localhost:8081/actuator/health
- OCR: http://localhost:8082/actuator/health
- NLP: http://localhost:8083/actuator/health
- Scoring: http://localhost:8084/actuator/health
- Feedback: http://localhost:8085/actuator/health

---

## 🐛 Problèmes connus

- **OCR Service not responding:** Vérifiez que Tesseract est bien installé
- **CORS errors:** Assurez-vous que le proxy Vite est correctement configuré
- **Service discovery issues:** Attendez 30 secondes pour que les services s'enregistrent dans Eureka
- **RabbitMQ connection refused:** Vérifiez que RabbitMQ est démarré sur le port 5672
