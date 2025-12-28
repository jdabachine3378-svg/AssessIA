# AssessAI - Frontend React

Application frontend React pour l'évaluation automatique de copies d'examen.

## Installation

```bash
npm install
```

## Démarrage

```bash
npm run dev
```

L'application sera accessible sur http://localhost:3000

## Configuration

- **Backend API**: http://172.20.128.1:8081 (configuré dans `src/services/api.js`)
- **Port Frontend**: 3000 (configuré dans `vite.config.js`)

## Fonctionnalités

- Authentification simple (demo mode avec localStorage)
- Interface Enseignant : OCR, NLP, Scoring, Feedback
- Interface Étudiant : consultation des résultats
- Export CSV des rapports
- Mock des endpoints API si le backend n'est pas disponible

## Technologies

- React 18
- Vite
- React Router
- Axios
- TailwindCSS
- Context API

