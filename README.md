# 🐳 HippoNuage

**HippoNuage** est une application de cloud personnel développée dans le cadre du titre RNCP 5 – Développeur Web et Web Mobile (Holberton School). Elle permet aux utilisateurs d’uploader, consulter, organiser, prévisualiser, télécharger et supprimer leurs fichiers dans un environnement sécurisé, moderne et responsive.

## 🚀 Fonctionnalités principales

### 🔐 Authentification
- Création de compte avec vérification d’email
- Connexion sécurisée (JWT + Blacklist tokens)
- Déconnexion, update mot de passe
- Middleware de vérification d’email avant actions sensibles

### 📁 Gestion de fichiers
- Upload de fichiers (compression côté serveur)
- Accès à la **liste de fichiers**
- Accès à un **fichier unique** (preview ou téléchargement selon les paramètres)
- Suppression de fichiers (stockage + base)
- Affichage dynamique sur le frontend (tableau réactif)

### 🧩 Frontend
- Angular 19.2 + TailwindCSS 3
- Composants UI + Composants routés
- Menu déroulant conditionnel (connecté vs déconnecté)
- Composant de validation d’email (cohérent avec la charte graphique)
- Dashboard interactif (chargement automatique des fichiers au refresh)
- Bouton de suppression de fichier
- Début de logique drag & drop (hors MVP mais prévu)

### 🛠 Backend
- Architecture en microservices (Spring Boot)
    - Auth Service
    - File Upload Service
    - File Access Service
- PostgreSQL avec tables : `users`, `files`, `tokens`, `email_validation_token`
- Hébergement :
    - Frontend → Vercel
    - Backend → Railway
    - Fichiers → AWS S3
- Sécurité :
    - JWT, hachage (BCrypt)
    - Validation des requêtes (Bean Validation)
    - Protection contre injection SQL (JPA + regex)

## 📦 Installation & Lancement

### Prérequis
- Node.js / Angular CLI
- Java 17+
- PostgreSQL
- Docker (optionnel mais recommandé)
- AWS S3 credentials

### Frontend
```bash
cd frontend
npm install
ng serve
```
### Backend
``` bash
cd backend
# Lancer chaque microservice séparément ou via Docker
./mvnw spring-boot:run
```
