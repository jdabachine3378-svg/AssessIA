# OCR Service - Microservice AssessAI

Microservice Spring Boot pour l'extraction de texte à partir d'images (OCR) utilisant Tesseract et RabbitMQ pour la communication asynchrone.

## 🚀 Fonctionnalités

- **Extraction de texte** à partir d'images (Base64, URL, ou upload de fichier)
- **Communication asynchrone** via RabbitMQ pour recevoir les demandes et publier les résultats
- **API REST** pour les appels synchrones
- **Support multi-langues** (français par défaut, configurable)
- **Niveau de confiance** pour les résultats OCR

## 📋 Prérequis

### 1. Java 21
Assurez-vous que Java 21 est installé sur votre système.

### 2. Tesseract OCR
Téléchargez et installez Tesseract OCR :

**Windows :**
- Télécharger depuis : https://github.com/UB-Mannheim/tesseract/wiki
- Installer dans `C:\Program Files\Tesseract-OCR\`
- Ajouter au PATH système
- Télécharger les fichiers de langue (fra.traineddata pour le français) et les placer dans `C:\Program Files\Tesseract-OCR\tessdata\`

**Linux (Ubuntu/Debian) :**
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr
sudo apt-get install tesseract-ocr-fra  # Pour le français
```

**macOS :**
```bash
brew install tesseract
brew install tesseract-lang  # Pour les langues
```

### 3. RabbitMQ
Installez et démarrez RabbitMQ :

**Avec Docker (recommandé) :**
```bash
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

**Ou installez localement :**
- Télécharger depuis : https://www.rabbitmq.com/download.html
- Démarrer le serveur RabbitMQ

L'interface de gestion est accessible sur : http://localhost:15672 (guest/guest)

## 🔧 Configuration

Le fichier `application.yaml` contient toutes les configurations nécessaires :

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5673
    username: guest
    password: guest

rabbitmq:
  exchange:
    name: assessai.exchange
  queue:
    ocr:
      request: assessai.ocr.request
      response: assessai.ocr.response

server:
  port: 8081
```

### Configuration Tesseract

Si Tesseract n'est pas dans le PATH système, configurez le chemin dans `application.yaml` :

```yaml
tesseract:
  data:
    path: C:/Program Files/Tesseract-OCR/tessdata  # Windows
    # path: /usr/share/tesseract-ocr/5/tessdata     # Linux
```

## 🏃 Exécution

### 1. Compiler le projet
```bash
cd ocr-service
mvn clean install
```

### 2. Démarrer le microservice
```bash
mvn spring-boot:run
```

Le service sera accessible sur : http://localhost:8081

## 📡 API REST

### 1. Traiter une image via Base64

**POST** `/api/ocr/process`

```json
{
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "language": "fra"
}
```

**Réponse :**
```json
{
  "extractedText": "Texte extrait de l'image",
  "confidence": 0.95,
  "success": true,
  "errorMessage": null,
  "processedAt": "2024-01-15T10:30:00",
  "processingTimeMs": 1500
}
```

### 2. Traiter une image via URL

**POST** `/api/ocr/process`

```json
{
  "imageUrl": "https://example.com/image.png",
  "language": "fra"
}
```

### 3. Upload de fichier

**POST** `/api/ocr/process/upload`

- Content-Type: `multipart/form-data`
- Paramètres:
  - `file`: Fichier image (PNG, JPG, etc.)
  - `language`: Langue (optionnel, défaut: "fra")

**Exemple avec cURL :**
```bash
curl -X POST http://localhost:8081/api/ocr/process/upload \
  -F "file=@image.png" \
  -F "language=fra"
```

### 4. Health Check

**GET** `/api/ocr/health`

Retourne : `OCR Service is running`

## 🔄 Communication RabbitMQ

### Architecture

Le service écoute les demandes d'OCR dans la queue `assessai.ocr.request` et publie les résultats dans `assessai.ocr.response`.

### Format des messages

#### Requête (OcrRequest)
```json
{
  "submissionId": "sub-123",
  "requestId": "req-456",
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "imageUrl": null,
  "language": "fra"
}
```

#### Réponse (OcrResponse)
```json
{
  "submissionId": "sub-123",
  "requestId": "req-456",
  "extractedText": "Texte extrait",
  "confidence": 0.95,
  "success": true,
  "errorMessage": null,
  "processedAt": "2024-01-15T10:30:00",
  "processingTimeMs": 1500
}
```

### Tester avec RabbitMQ

Pour envoyer un message de test via RabbitMQ Management UI :

1. Aller sur http://localhost:15672
2. Se connecter (guest/guest)
3. Aller dans "Exchanges" → `assessai.exchange`
4. Publier un message avec routing key `ocr.request`

## 🧪 Tests

```bash
mvn test
```

## 📦 Structure du projet

```
ocr-service/
├── src/
│   ├── main/
│   │   ├── java/com/assessai/
│   │   │   ├── config/          # Configuration RabbitMQ
│   │   │   ├── controller/      # Contrôleurs REST
│   │   │   ├── dto/             # DTOs pour API REST
│   │   │   ├── listener/        # Listeners RabbitMQ
│   │   │   ├── model/           # Modèles pour RabbitMQ
│   │   │   ├── service/         # Services métier
│   │   │   └── OcrServiceApplication.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
└── pom.xml
```

## 🔍 Langues supportées

Le service supporte toutes les langues installées avec Tesseract. Langues courantes :

- `fra` - Français
- `eng` - Anglais
- `spa` - Espagnol
- `deu` - Allemand
- `ita` - Italien
- etc.

## 🐛 Dépannage

### Erreur : "Tesseract not found"
- Vérifiez que Tesseract est installé et dans le PATH
- Ou configurez `tesseract.data.path` dans `application.yaml`

### Erreur : "Language not found"
- Vérifiez que le fichier de langue (ex: `fra.traineddata`) est présent dans le dossier `tessdata`
- Téléchargez depuis : https://github.com/tesseract-ocr/tessdata

### Erreur de connexion RabbitMQ
- Vérifiez que RabbitMQ est démarré : `docker ps` ou vérifier le service
- Vérifiez les credentials dans `application.yaml`

## 📝 Notes

- Le niveau de confiance est une approximation basée sur la longueur du texte extrait
- Pour une meilleure précision, utilisez des images haute résolution (300 DPI recommandé)
- Les formats supportés : PNG, JPG, JPEG, TIFF, BMP

## 🤝 Contribution

Ce microservice fait partie du projet AssessAI - Système Automatique de Correction et d'Évaluation de Copies.

