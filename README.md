<div align="center">

# 🧾 SmartBilling

**API REST de facturation sécurisée — Production Ready**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-7-brightgreen?logo=springsecurity)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-black?logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?logo=swagger)](http://localhost:8081/swagger-ui.html)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

*Développé par [Ayette Boubaya](https://github.com/AyetteBoubaya)*

</div>

---

## 📋 Description

SmartBilling est une API REST complète de gestion de facturation construite avec **Spring Boot 4.0.2**.  
Elle expose un module d'authentification production-ready avec JWT, refresh token rotation, vérification d'email et reset de mot de passe.

### Ce que ce projet démontre

- Architecture en couches (Controller → Service → Repository → Domain)
- Sécurité stateless avec JWT + Spring Security 7
- Refresh token rotation avec détection de réutilisation (security pattern)
- Tests unitaires et d'intégration (JUnit 6, Mockito, H2 in-memory)
- Conteneurisation complète avec Docker et docker-compose
- Documentation interactive avec Swagger/OpenAPI 3

---

## 🛠️ Stack technique

| Catégorie | Technologie |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.2 |
| Sécurité | Spring Security 7 + JWT (jjwt 0.12.3) |
| Base de données | PostgreSQL 16 |
| ORM | Hibernate 7 + Spring Data JPA |
| Tests | JUnit 6 + Mockito 5 + H2 |
| Documentation | Springdoc OpenAPI 3 / Swagger UI |
| Build | Maven 3.9 |
| Conteneurisation | Docker + docker-compose |
| Email | Spring Mail + Gmail SMTP |

---

## 🚀 Démarrage rapide

### Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et démarré
- C'est tout !

### Lancer l'application

```bash
git clone https://github.com/AyetteBoubaya/smartbilling.git
cd smartbilling
docker-compose up --build
```

L'application démarre sur **http://localhost:8081**  
Swagger UI disponible sur **http://localhost:8081/swagger-ui.html**

### Sans Docker (développement local)

```bash
# Prérequis : Java 17, Maven, PostgreSQL local
mvn spring-boot:run
```

---

## 🔐 Module Auth — Endpoints

### Authentification publique

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Inscription + envoi email de vérification |
| `POST` | `/api/auth/login` | Connexion → accessToken + refreshToken |
| `GET` | `/api/auth/verify-email?token=` | Vérification de l'email |
| `POST` | `/api/auth/resend-verification` | Renvoyer l'email de vérification |
| `POST` | `/api/auth/refresh` | Renouveler l'accessToken |
| `POST` | `/api/auth/logout` | Révoquer le refreshToken |
| `POST` | `/api/auth/forgot-password` | Envoi lien + OTP reset password |
| `POST` | `/api/auth/reset-password/link` | Reset via lien |
| `POST` | `/api/auth/reset-password/otp` | Reset via code OTP |

### Utilisateurs (JWT requis — ADMIN)

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/users/{email}` | Récupérer un utilisateur |
| `PUT` | `/api/users/{email}` | Modifier un utilisateur |
| `DELETE` | `/api/users/{email}` | Supprimer un utilisateur |

---

## 💡 Exemples d'utilisation

### Inscription

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!",
    "role": "USER"
  }'
```

### Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!"
  }'
```

Réponse :

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "role": "USER",
  "emailVerified": true
}
```

### Requête authentifiée

```bash
curl -X GET http://localhost:8081/api/users/user@example.com \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🏗️ Architecture

```
src/
├── auth/
│   ├── config/          # SecurityConfig, OpenApiConfig
│   ├── controller/      # AuthController, PasswordController, UserController
│   ├── domain/          # User, Token, TokenType
│   ├── dto/
│   │   ├── requests/    # UserRequest, LoginRequest, ForgotPasswordRequest...
│   │   └── responses/   # AuthResponse, UserResponse, MessageResponse...
│   ├── exception/       # GlobalExceptionHandler
│   ├── repository/      # UserRepository, TokenRepository
│   ├── security/        # JwtService, JwtAuthFilter, UserDetailsServiceImpl
│   └── service/         # AuthService, PasswordService, UserService, EmailService
└── refresh/
    ├── domain/          # RefreshToken
    ├── repository/      # RefreshTokenRepository
    └── service/         # RefreshTokenService
```

---

## 🔄 Flux d'authentification

```
Client                    API                      DB
  |                        |                        |
  |-- POST /register ----->|                        |
  |                        |-- save user ---------->|
  |                        |-- send email --------->|
  |<-- 201 Created --------|                        |
  |                        |                        |
  |-- POST /login -------->|                        |
  |                        |-- find user ---------->|
  |                        |-- validate BCrypt ---->|
  |                        |-- generate JWT ------->|
  |                        |-- save refreshToken -->|
  |<-- 200 {tokens} -------|                        |
  |                        |                        |
  |-- GET /api/... ------->|                        |
  |   Authorization: Bearer|                        |
  |                        |-- validate JWT         |
  |                        |-- inject SecurityCtx   |
  |<-- 200 {data} ---------|                        |
```

---

## 🔒 Sécurité

### JWT
- **Access token** : durée de vie 15 minutes
- **Refresh token** : durée de vie 7 jours, stocké en base
- **Rotation** : chaque refresh révoque l'ancien token et génère un nouveau
- **Détection de réutilisation** : si un token révoqué est présenté, tous les tokens de l'utilisateur sont révoqués

### Bonnes pratiques implémentées
- Mots de passe hachés avec BCrypt
- Anti-énumération d'emails sur forgot-password
- CSRF désactivé (JWT stateless)
- CORS configuré
- Validation des inputs avec Jakarta Validation

---

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Résultat attendu
Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
```

| Classe de test | Type | Description |
|---|---|---|
| `JwtServiceTest` | Unitaire | Génération et validation JWT |
| `AuthServiceImplTest` | Unitaire | Logique register/login/verify |
| `PasswordServiceImplTest` | Unitaire | Forgot/reset password |
| `AuthControllerIntegrationTest` | Intégration | Endpoints HTTP + sécurité |
| `UserControllerIntegrationTest` | Intégration | Autorisations ADMIN |

---

## ⚙️ Variables d'environnement

| Variable | Description | Défaut |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/smartbilling` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur DB | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe DB | — |
| `APP_JWT_SECRET` | Secret JWT (256 bits min) | — |
| `APP_JWT_EXPIRATION_MS` | Durée access token (ms) | `900000` (15 min) |
| `APP_JWT_REFRESH_EXPIRATION_MS` | Durée refresh token (ms) | `604800000` (7 jours) |
| `SPRING_MAIL_USERNAME` | Email Gmail | — |
| `SPRING_MAIL_PASSWORD` | App password Gmail | — |

---

## 📄 Licence

MIT License — voir [LICENSE](LICENSE)

---

<div align="center">

**SmartBilling** — Développé avec ❤️ par [Ayette Boubaya](https://github.com/AyetteBoubaya)

</div>
