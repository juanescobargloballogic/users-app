# User Management API

This project is a Spring Boot RESTful API that allows user sign-up and login using JWT-based authentication. It uses an in-memory H2 database.

---

## Technologies

- Java 11
- Spring Boot 2.5.14
- Gradle 7.4
- H2 Database
- Spring Data JPA
- Spring Security (JWT)
- SLF4J for logging

---

## Build Instructions

Make sure you have:

- Java 11
- Gradle 7.4

### Build the Project

```bash
./gradlew build
```

### Run the Project locally
```bash
./gradlew bootRun
```
The application will be available at:
http://localhost:8080

### Run the Jacoco report
```bash
./gradlew test jacocoTestReport
```
After running the command, open this file in your browser to see the jacoco report:
```
build/reports/jacoco/test/html/index.html
```

## Endpoints

### sign-up
Registers a new user.
```
POST /users/sign-up
```

### login
Retrieves user info using a valid JWT token (sent as Authorization: Bearer <token>)
```
GET /users/login
```





