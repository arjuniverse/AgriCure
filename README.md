# AgriCure — Plant Disease Identification & Management

AgriCure is a full-stack plant disease identification and management platform designed to help growers explore supported plants, study disease information, and identify potential diseases based on observed symptoms.

The platform provides a centralized system for plant and disease management, symptom-based identification, secure authentication, and administrator-controlled catalogues.

## Live Demo

> 

## Features

* **Plant Catalogue:** Browse and search supported plants.
* **Disease Catalogue:** Explore disease records, symptoms, treatments, and prevention information.
* **Disease Identification:** Match observed symptoms to likely plant diseases.
* **JWT Authentication:** Secure user authentication with role-based access.
* **Role Management:** Separate `USER` and `ADMIN` permissions.
* **Admin Dashboard APIs:** Manage plants, diseases, mappings, and users.
* **REST API:** JSON-based communication between the frontend and backend.
* **Database Support:** H2 for local development and MySQL for production.
* **Validation & Error Handling:** Standardized JSON error responses through a global exception handler.

## Tech Stack

| Layer          | Technologies                               |
| -------------- | ------------------------------------------ |
| Backend        | Java 21, Spring Boot 4                     |
| Security       | Spring Security, JWT (HS256)               |
| Data Access    | Spring Data JPA                            |
| Database       | MySQL (Production), H2 (Local Development) |
| Frontend       | React, Vite                                |
| API            | REST JSON                                  |
| Authentication | JWT with `USER` and `ADMIN` roles          |

## Project Structure

```text
AgriCure/
├── backend/
│   └── Spring Boot REST API
├── frontend/
│   └── React + Vite Client
└── docs/
    ├── schema.sql
    └── agricure-system-architecture.png
```

## System Architecture

The application follows a layered architecture in which controllers receive API requests, services handle business logic, repositories communicate with the database, and DTOs provide structured API responses.

![AgriCure System Architecture](docs/agricure-system-architecture.png)

### Architecture Flow

```text
User
  ↓
React + Vite Frontend
  ↓
REST API Requests
  ↓
Spring Security + JWT Authentication
  ↓
Controllers
  ↓
Service Layer
  ↓
Business Logic and Validation
  ↓
Spring Data JPA Repositories
  ↓
Database
  ├── H2 (Local Development)
  └── MySQL (Production)
  ↓
API Response
  ↓
Frontend Displays Results
```

### Disease Identification Workflow

1. The authenticated user selects a plant.
2. The user submits observed symptoms and an optional description.
3. The backend retrieves diseases mapped to the selected plant.
4. The identification service compares symptom keywords and free-text tokens.
5. Diseases are scored based on overlapping symptoms.
6. Ranked matches are returned with treatment and prevention information.
7. The frontend displays the identification results.

## Getting Started

### Prerequisites

Make sure the following tools are installed:

* Java 21 or later
* Node.js 18 or later
* Maven Wrapper (included in the backend)
* MySQL (required only for MySQL-based execution)

## Run Locally

### 1. Start the Backend API

The fastest development setup uses the H2 file database and does not require MySQL.

#### Linux / macOS

```bash
cd backend
./mvnw spring-boot:run
```

#### Windows

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

H2 Console:

```text
http://localhost:8080/h2-console
```

H2 JDBC URL:

```text
jdbc:h2:file:./data/agricure
```

### Seeded Accounts

| Role  | Email                                             | Password   |
| ----- | ------------------------------------------------- | ---------- |
| ADMIN | [admin@agricure.com](mailto:admin@agricure.com)   | Admin@123  |
| USER  | [farmer@agricure.com](mailto:farmer@agricure.com) | Farmer@123 |

> These credentials are intended for local development and testing. Change or disable seeded credentials before production deployment.

### 2. Start the Frontend

Open a new terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the frontend at:

```text
http://localhost:5173
```

The Vite development server proxies `/api` requests to the backend.

## MySQL Setup

### 1. Create the Database

You can create the database manually using the following SQL command:

```sql
CREATE DATABASE agricure
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Alternatively, use the `createDatabaseIfNotExist=true` option in the JDBC URL.

### 2. Apply the Database Schema

The schema is available at:

```text
docs/schema.sql
```

You can apply the schema manually if you prefer explicit DDL. Hibernate can also create or update database tables depending on the configured settings.

### 3. Start the Backend with MySQL

#### Windows

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE="mysql"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="your_password"
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS

```bash
cd backend
SPRING_PROFILES_ACTIVE=mysql MYSQL_USER=root MYSQL_PASSWORD=your_password ./mvnw spring-boot:run
```

### Default JDBC URL

```text
jdbc:mysql://localhost:3306/agricure?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

## REST API

### Public Endpoints

| Method | Endpoint                      | Description               |
| ------ | ----------------------------- | ------------------------- |
| POST   | `/api/auth/register`          | Register a new user       |
| POST   | `/api/auth/login`             | Authenticate a user       |
| GET    | `/api/plants`                 | Retrieve supported plants |
| GET    | `/api/plants?search=tomato`   | Search plants             |
| GET    | `/api/plants/{id}`            | Retrieve a plant by ID    |
| GET    | `/api/diseases`               | Retrieve diseases         |
| GET    | `/api/diseases?search=blight` | Search diseases           |
| GET    | `/api/diseases/{id}`          | Retrieve a disease by ID  |

### Authenticated Endpoints

Authentication header:

```http
Authorization: Bearer <token>
```

| Method | Endpoint        | Description                                |
| ------ | --------------- | ------------------------------------------ |
| GET    | `/api/users/me` | Retrieve the current user                  |
| POST   | `/api/identify` | Identify likely diseases based on symptoms |

Example identification request:

```json
{
  "plantId": 1,
  "symptoms": [
    "yellow leaves",
    "brown spots"
  ],
  "description": "Leaves show yellowing and brown spots."
}
```

### Admin-Only Endpoints

| Method | Endpoint              | Description                     |
| ------ | --------------------- | ------------------------------- |
| POST   | `/api/admin/plants`   | Create a plant                  |
| PUT    | `/api/admin/plants`   | Update a plant                  |
| DELETE | `/api/admin/plants`   | Delete a plant                  |
| POST   | `/api/admin/diseases` | Create a disease                |
| PUT    | `/api/admin/diseases` | Update a disease                |
| DELETE | `/api/admin/diseases` | Delete a disease                |
| GET    | `/api/admin/mappings` | Retrieve plant-disease mappings |
| POST   | `/api/admin/mappings` | Create a mapping                |
| PUT    | `/api/admin/mappings` | Update a mapping                |
| DELETE | `/api/admin/mappings` | Delete a mapping                |
| GET    | `/api/users`          | Retrieve users                  |

## Backend Architecture

AgriCure follows a layered backend architecture:

### Controllers

Handle incoming REST requests and return API responses.

### Services

Contain business logic, validation, disease identification, and application workflows.

### Repositories

Use Spring Data JPA to interact with the database.

### DTOs

Transfer structured data between the API and clients without exposing entities directly through write APIs.

### Security

Spring Security and JWT authentication protect authenticated routes and enforce `USER` and `ADMIN` roles.

### Global Exception Handling

`GlobalExceptionHandler` converts application errors into standardized JSON responses, including:

* Not-found errors
* Duplicate records
* Validation failures
* Authentication and authorization errors

## Disease Identification Logic

The identification service uses the following process:

```text
Selected Plant
    ↓
Retrieve Mapped Diseases
    ↓
Read Submitted Symptoms
    ↓
Process Free-Text Description
    ↓
Compare Symptom Keywords
    ↓
Calculate Disease Scores
    ↓
Rank Potential Matches
    ↓
Return Treatment and Prevention Information
```

The system scores diseases based on overlapping symptom keywords and free-text tokens for diseases mapped to the selected plant.

## Testing

Run the backend test suite:

### Linux / macOS

```bash
cd backend
./mvnw test
```

### Windows

```powershell
cd backend
.\mvnw.cmd test
```

### Covered Flows

* User registration
* User login and JWT authentication
* Public plant search
* Disease identification matching
* Role-protected admin plant CRUD operations

## Database Options

| Environment       | Database         |
| ----------------- | ---------------- |
| Local Development | H2 File Database |
| Production        | MySQL            |

The H2 configuration is intended to provide a quick local setup, while MySQL is supported for production deployments.

## Future Improvements

Potential future enhancements include:

* Image-based plant disease detection
* More advanced symptom classification
* Multilingual support for growers
* Crop health monitoring
* Disease history and identification reports
* Expanded plant and disease datasets
* Mobile-friendly farmer workflows

## License

Add the project's license information here.

## Contributors

Add contributor names and project information here.
