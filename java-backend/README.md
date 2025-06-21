# EMart Java Backend

This is a standalone Java backend project created for GitHub language statistics. It is not connected to the main EMart frontend project and serves only to demonstrate Java development skills.

## Project Structure

```
java-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── emart/
│       │           └── backend/
│       │               ├── EmartBackendApplication.java
│       │               ├── controller/
│       │               │   └── UserController.java
│       │               ├── model/
│       │               │   ├── User.java
│       │               │   └── Product.java
│       │               └── service/
│       │                   └── UserService.java
│       └── resources/
│           └── application.properties
├── pom.xml
└── README.md
```

## Features

- **Spring Boot Application**: Main application class with Spring Boot configuration
- **REST Controllers**: RESTful API endpoints for user management
- **Entity Models**: JPA entities for User and Product
- **Service Layer**: Business logic implementation
- **Configuration**: Application properties and database configuration

## Technology Stack

- **Java 11**
- **Spring Boot 2.7.0**
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **Maven** for dependency management

## Purpose

This project is created solely for:
- Demonstrating Java development skills
- Contributing to GitHub language statistics
- Showcasing Spring Boot knowledge
- Providing a reference for Java backend architecture

## Note

This backend is **NOT CONNECTED** to the main EMart frontend project. It runs independently and does not affect the functionality of the main application.

## Running the Application

To run this standalone backend:

```bash
cd java-backend
mvn spring-boot:run
```

The application will start on port 8081 with context path `/api`.

## API Endpoints

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Database

Uses H2 in-memory database for development purposes. Database console available at `http://localhost:8081/api/h2-console`. 