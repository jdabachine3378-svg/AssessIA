# Guide pour lancer RabbitMQ

## Option 1 : RabbitMQ est déjà lancé ✅

Votre conteneur RabbitMQ est **déjà en cours d'exécution** !

**Statut actuel :**
- Conteneur : `assessai-rabbitmq`
- Port AMQP : `5673` (au lieu de 5672)
- Port Management UI : `15672`
- Statut : ✅ En cours d'exécution

## Accéder à l'interface de gestion RabbitMQ

Ouvrez votre navigateur et allez à :
```
http://localhost:15672
```

**Identifiants :**
- Username : `guest`
- Password : `guest`

## Option 2 : Lancer RabbitMQ avec docker-compose

Pour lancer RabbitMQ, MySQL et Redis ensemble :

```bash
cd SCORING-SERVICE
docker-compose up -d rabbitmq
```

Ou pour tout lancer :
```bash
docker-compose up -d
```

## Option 3 : Lancer RabbitMQ seul avec Docker

Si le conteneur n'existe pas :

```bash
docker run -d --name assessai-rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3-management-alpine
```

## Commandes utiles

### Vérifier le statut
```bash
docker ps --filter "name=assessai-rabbitmq"
```

### Arrêter RabbitMQ
```bash
docker stop assessai-rabbitmq
```

### Démarrer RabbitMQ
```bash
docker start assessai-rabbitmq
```

### Voir les logs
```bash
docker logs assessai-rabbitmq
```

### Supprimer et recréer (si nécessaire)
```bash
docker stop assessai-rabbitmq
docker rm assessai-rabbitmq
docker run -d --name assessai-rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3-management-alpine
```

## ⚠️ Note importante sur le port

Votre conteneur actuel utilise le port **5673** au lieu de **5672**. 

Si vous voulez utiliser le port standard 5672, vous devez :
1. Arrêter le conteneur actuel
2. Le supprimer
3. Le recréer avec le bon port

Ou modifier `application.yaml` pour utiliser le port 5673 :
```yaml
spring:
  rabbitmq:
    port: 5673
```

