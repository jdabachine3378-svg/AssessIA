# Guide de Démarrage - OCR Service

## ✅ État Actuel

Votre microservice OCR est **déjà démarré** et fonctionne sur le port **8082** !

Les erreurs RabbitMQ que vous voyez sont **normales** si RabbitMQ n'est pas démarré. L'application continue de fonctionner et l'**API REST est disponible**.

## 🚀 Options de Démarrage

### Option 1: Utiliser uniquement l'API REST (sans RabbitMQ)

Vous pouvez utiliser l'API REST directement sans démarrer RabbitMQ :

1. **Tester l'endpoint de santé :**
   ```
   GET http://localhost:8082/api/ocr/health
   ```

2. **Tester l'OCR avec une image Base64 ou URL :**
   ```
   POST http://localhost:8082/api/ocr/process
   Content-Type: application/json
   
   {
     "imageBase64": "...",
     "language": "fra"
   }
   ```

### Option 2: Démarrer RabbitMQ (pour la communication asynchrone)

#### Avec Docker Compose (Recommandé)

1. **Démarrer RabbitMQ :**
   ```bash
   cd ocr-service
   docker-compose up -d
   ```

2. **Vérifier que RabbitMQ est démarré :**
   - Interface web : http://localhost:15672 (guest/guest)
   - L'application se reconnectera automatiquement

#### Sans Docker

1. **Installer RabbitMQ localement :**
   - Windows : Télécharger depuis https://www.rabbitmq.com/download.html
   - Linux : `sudo apt-get install rabbitmq-server && sudo systemctl start rabbitmq-server`

2. **Démarrer RabbitMQ :**
   ```bash
   # Windows (si installé comme service)
   net start RabbitMQ
   
   # Linux
   sudo systemctl start rabbitmq-server
   ```

## 📝 Vérification

Une fois RabbitMQ démarré, vous devriez voir dans les logs :
```
Connection to broker established
```

Au lieu de :
```
Connection refused: getsockopt
```

## 🧪 Tester le Service

### 1. Health Check
```bash
curl http://localhost:8082/api/ocr/health
```

### 2. Test OCR via API REST
Utilisez Postman, cURL, ou le fichier `test-api.http` fourni.

### 3. Test RabbitMQ (si démarré)
- Aller sur http://localhost:15672
- Se connecter (guest/guest)
- Aller dans "Exchanges" → `assessai.exchange`
- Publier un message avec le contenu de `rabbitmq-test-message.json`

## ⚠️ Notes Importantes

- **L'API REST fonctionne sans RabbitMQ** - vous pouvez tester immédiatement
- **RabbitMQ est nécessaire** uniquement pour la communication asynchrone entre microservices
- Les tentatives de reconnexion automatique sont normales et ne bloquent pas l'application
- Le service écoute automatiquement les messages sur la queue `assessai.ocr.request`

## 🔧 Configuration

Toute la configuration est dans `application.yaml`. Les paramètres importants :
- Port : 8082
- RabbitMQ : localhost:5673
- Eureka : Désactivé par défaut

