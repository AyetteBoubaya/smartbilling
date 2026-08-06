<div align="center">

# 🧾 SmartBilling

**API REST de facturation sécurisée — Production Ready**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.2-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-7-brightgreen?logo=springsecurity)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-black?logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?logo=swagger)](https://smartbilling-production.up.railway.app/swagger-ui.html)
[![Railway](https://img.shields.io/badge/Railway-Deployed-blueviolet?logo=railway)](https://smartbilling-production.up.railway.app)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

*Développé par [Ayette Boubaya](https://github.com/AyetteBoubaya)*

**🔗 API live : [smartbilling-production.up.railway.app](https://smartbilling-production.up.railway.app/swagger-ui.html)**

</div>

---

## 📋 Description

SmartBilling est une **API REST complète de gestion de facturation** construite avec Spring Boot 4.0.2 et déployée en production sur Railway.

Elle couvre l'ensemble du cycle de vie d'une application de facturation B2B : authentification sécurisée, gestion des clients et produits, création et suivi des factures avec génération PDF.

### Ce que ce projet démontre

- Architecture en couches (Controller → Service → Repository → Domain)
- Sécurité stateless avec JWT + Spring Security 7
- Refresh token rotation avec détection de réutilisation
- Gestion des clients B2B avec pagination et recherche
- Catalogue produits avec calcul automatique TVA/TTC (BigDecimal)
- Facturation complète avec relations JPA, cycle de vie et génération PDF
- Tests unitaires et d'intégration (JUnit 6, Mockito, H2)
- Conteneurisation avec Docker et docker-compose
- Documentation interactive Swagger/OpenAPI 3
- Déploiement continu sur Railway (CI/CD via GitHub)

---

## 🛠️ Stack technique

| Catégorie | Technologie |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.2 |
| Sécurité | Spring Security 7 + JWT (jjwt 0.12.3) |
| Base de données | PostgreSQL 16 |
| ORM | Hibernate 7 + Spring Data JPA |
| Tests | JUnit 6 + Mockito 5 + H2 in-memory |
| Documentation | Springdoc OpenAPI 3 / Swagger UI |
| Build | Maven 3.9 |
| Conteneurisation | Docker + docker-compose |
| Email | Spring Mail + Gmail SMTP |
| PDF | iTextPDF 7 |
| Déploiement | Railway |

---

## 🚀 Démarrage rapide

### Option 1 — API live (aucune installation)

Testez directement l'API déployée sur Railway :

```
https://smartbilling-production.up.railway.app/swagger-ui.html
```

### Option 2 — Docker (recommandé en local)

```bash
git clone https://github.com/AyetteBoubaya/smartbilling.git
cd smartbilling
docker-compose up --build
```

L'application démarre sur **http://localhost:8081**
Swagger UI disponible sur **http://localhost:8081/swagger-ui.html**

### Option 3 — Sans Docker

```bash
# Prérequis : Java 17, Maven, PostgreSQL local
mvn spring-boot:run
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
│   ├── repository/      # UserRepository, TokenRepository
│   ├── security/        # JwtService, JwtAuthFilter, UserDetailsServiceImpl
│   └── service/         # AuthService, PasswordService, UserService, EmailService
├── refresh/
│   ├── domain/          # RefreshToken
│   ├── repository/      # RefreshTokenRepository
│   └── service/         # RefreshTokenService
├── client/
│   ├── controller/      # ClientController
│   ├── domain/          # Client
│   ├── dto/             # ClientRequest, ClientResponse
│   ├── repository/      # ClientRepository
│   └── service/         # ClientService, ClientServiceImpl
├── product/
│   ├── controller/      # ProductController
│   ├── domain/          # Product, ProductCategory
│   ├── dto/             # ProductRequest, ProductResponse
│   ├── repository/      # ProductRepository
│   └── service/         # ProductService, ProductServiceImpl
├── invoice/
│   ├── controller/      # InvoiceController
│   ├── domain/          # Invoice, InvoiceItem, InvoiceStatus
│   ├── dto/             # InvoiceRequest, InvoiceResponse, InvoiceItemRequest...
│   ├── repository/      # InvoiceRepository
│   └── service/         # InvoiceService, InvoiceServiceImpl
└── shared/
    └── exception/       # GlobalExceptionHandler, ResourceNotFoundException...
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

## 🏢 Module Clients — Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/clients` | Créer un client (ADMIN) |
| `GET` | `/api/clients` | Lister avec pagination et recherche |
| `GET` | `/api/clients?search=dupont` | Recherche par nom ou email |
| `GET` | `/api/clients?page=0&size=10` | Pagination |
| `GET` | `/api/clients/{id}` | Récupérer un client |
| `PUT` | `/api/clients/{id}` | Modifier un client (ADMIN) |
| `DELETE` | `/api/clients/{id}` | Supprimer un client (ADMIN) |

---

## 📦 Module Produits — Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/products` | Créer un produit (ADMIN) |
| `GET` | `/api/products` | Lister avec pagination et recherche |
| `GET` | `/api/products?category=SERVICE` | Filtrer par catégorie |
| `GET` | `/api/products/{id}` | Récupérer un produit |
| `PUT` | `/api/products/{id}` | Modifier un produit (ADMIN) |
| `DELETE` | `/api/products/{id}` | Supprimer un produit (ADMIN) |

Catégories disponibles : `SERVICE` `FOURNITURE` `LOGICIEL` `MATERIEL` `CONSEIL` `AUTRE`

---

## 🧾 Module Factures — Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/invoices` | Créer une facture (ADMIN) |
| `GET` | `/api/invoices` | Lister avec filtres et pagination |
| `GET` | `/api/invoices?status=SENT` | Filtrer par statut |
| `GET` | `/api/invoices?customerId=1` | Factures d'un client |
| `GET` | `/api/invoices/{id}` | Récupérer une facture |
| `PATCH` | `/api/invoices/{id}/status` | Changer le statut |
| `DELETE` | `/api/invoices/{id}` | Supprimer (DRAFT uniquement) |
| `GET` | `/api/invoices/{id}/pdf` | Télécharger le PDF |

### Cycle de vie d'une facture

```
DRAFT → SENT → PAID
          ↓      ↓
        OVERDUE  CANCELLED
```

---

## 💡 Exemples d'utilisation

### Inscription

```bash
curl -X POST https://smartbilling-production.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!",
    "role": "USER"
  }'
```

### Login

```bash
curl -X POST https://smartbilling-production.up.railway.app/api/auth/login \
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

### Créer une facture

```bash
curl -X POST https://smartbilling-production.up.railway.app/api/invoices \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      { "productId": 1, "quantity": 2 }
    ],
    "issueDate": "2026-08-06",
    "dueDate": "2026-09-06"
  }'
```

Réponse :

```json
{
  "id": 1,
  "invoiceNumber": "INV-2026-0001",
  "customerName": "Dupont SA",
  "status": "DRAFT",
  "totalHT": 2000.00,
  "totalTVA": 400.00,
  "totalTTC": 2400.00,
  "items": [...]
}
```

### Télécharger le PDF

```bash
curl -X GET https://smartbilling-production.up.railway.app/api/invoices/1/pdf \
  -H "Authorization: Bearer <token>" \
  --output facture-1.pdf
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
- Exceptions typées avec codes HTTP appropriés (400, 401, 403, 404, 409)

---

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test
```

| Classe de test | Type | Description |
|---|---|---|
| `JwtServiceTest` | Unitaire | Génération et validation JWT |
| `AuthServiceImplTest` | Unitaire | Logique register/login/verify |
| `PasswordServiceImplTest` | Unitaire | Forgot/reset password |
| `AuthControllerIntegrationTest` | Intégration | Endpoints HTTP + sécurité |
| `UserControllerIntegrationTest` | Intégration | Autorisations ADMIN |
| `ClientServiceImplTest` | Unitaire | CRUD clients + pagination |
| `ProductServiceImplTest` | Unitaire | CRUD produits + calcul TTC |
| `InvoiceServiceImplTest` | Unitaire | Factures + numérotation auto |

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
| `SPRING_MAIL_HOST` | Serveur SMTP | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` | Port SMTP | `587` |
| `SPRING_MAIL_USERNAME` | Email Gmail | — |
| `SPRING_MAIL_PASSWORD` | App password Gmail | — |
| `APP_FRONTEND_URL` | URL du frontend (CORS) | `http://localhost:4200` |
| `SERVER_PORT` | Port de l'application | `8081` |

---

## 📄 Licence

MIT License — voir [LICENSE](LICENSE)

---

<div align="center">

**SmartBilling** — Développé avec ❤️ par [Ayette Boubaya](https://github.com/AyetteBoubaya)

🔗 [API Live](https://smartbilling-production.up.railway.app/swagger-ui.html) · 💻 [GitHub](https://github.com/AyetteBoubaya/smartbilling)

</div>
