# ProjectLibrary - REST API

This project is a REST API for a library management system, built with a Contract-First approach using OpenAPI 3.1.0.

## Tech Stack

- Java 21
- Spring Boot 3.3.x
- Spring Web (REST)
- Spring Data JPA + Hibernate
- PostgreSQL 15+
- Maven 4.0.0+
- Docker

## Prerequisites

- Java SDK 21
- Maven 4.0.0+
- Docker

## Local Development

### Running the application

To run the application locally, you will need to have a PostgreSQL database running. You can start one using Docker:

```bash
docker-compose up -d postgres
```

Then, you can run the application using Maven:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`. The Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.

### Running the tests

To run the tests, use the following command:

```bash
./mvnw test
```

## Docker Deployment

To build and run the application using Docker, use the following command:

```bash
docker-compose up -d --build
```

The application will be available at `http://localhost:8080`. The Swagger UI will be available at `http://localhost:8080/swagger-ui.html`.

To stop the application, use the following command:

```bash
docker-compose down
```
