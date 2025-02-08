[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/K6MEbnQdqEgQE7qSJFetp9/H33kUaGmkCgFib3RGNfUhB/tree/main.svg?style=svg&circle-token=CCIPRJ_7ji7WNuGheaWcaRZXm1rDD_c9615adce49760fc5f79c4b3609672be13f74c90)](https://dl.circleci.com/status-badge/redirect/circleci/K6MEbnQdqEgQE7qSJFetp9/H33kUaGmkCgFib3RGNfUhB/tree/main)
[![codecov](https://codecov.io/gh/MiroslavKolosnjaji/transaction-service/graph/badge.svg?token=rQ2XT2puXs)](https://codecov.io/gh/MiroslavKolosnjaji/transaction-service)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
![GitHub forks](https://img.shields.io/github/forks/MiroslavKolosnjaji/transaction-service)

# transaction-service

**TransactionService** is a microservice within a larger banking system project.
It is primarily responsible for managing and processing financial transactions, such as deposits, withdrawals, and transfers.
This microservice ensures that transaction requests are handled efficiently and accurately, interacting with other services to validate users and update account balances.

## Technologies

- **Spring Boot:** Core framework for building enterprise Java applications.
- **Spring Boot DevTools:** Tools for improving development efficiency with features like automatic restarts and live reload.
- **Spring Boot Validation:** Ensures data integrity with rule-based constraints.
- **Spring JPA:** Simplifies database interactions using the Java Persistence API.
- **Spring WebFlux:** Used in a synchronous manner for a lightweight and flexible architecture.
- **MapStruct:** Used for mapping between domain entities and DTOs.
- **Lombok:** Library for reducing boilerplate code in Java.
- **Apache Kafka:** Event streaming platform used for communication between services in an event-driven architecture.
- **PostgreSQL:** Relational database management system used for production-grade storage.
- **Flyway:**  Database migration tool for version-controlled schema changes.
- **H2 Database:** In-memory relational database for development and testing purposes.
- **JUnit5:** Testing framework for unit and integration testing in Java.
- **Mockito:** Framework for creating mock objects in automated testing.
- **Wiremock:** Mocking tool for simulating HTTP-based APIs in integration tests.
- **MockWebServer:** Lightweight server for testing HTTP clients, primarily used with WebClient.
- **Testcontainers:** Library for running lightweight, disposable containers for database and service integration tests.
- **Docker:** A platform for building, distributing, and running applications in containers.


## Service Communication

TransactionService acts as the "transaction manager" within the banking system. It communicates synchronously with two key microservices: **AccountService** and **UserService**.

- **AccountService:** TransactionService sends requests to **AccountService** to perform **deposit**, **withdraw**, and **transfer** operations, ensuring that account balances are accurately updated and the integrity of transactions is maintained.


- **UserService:** Before performing any transaction, TransactionService sends a request to **UserService** to verify if the user exists in the system. This ensures that only valid users can perform transactions, adding an additional layer of security and validation.

Additionally, **TransactionService** interacts asynchronously with the **NotificationService** via **Apache Kafka** to notify users about the outcome of their transactions.

This setup guarantees secure, reliable, and transparent transactions while ensuring the accuracy of user and account data within the system.

For more information on **UserService**, visit the [user-service repository](https://github.com/MiroslavKolosnjaji/user-service).

For more information on **AccountService**, visit the [account-service repository](https://github.com/MiroslavKolosnjaji/account-service).

For more information on **NotificationService**, visit the [notification-service repository](https://github.com/MiroslavKolosnjaji/notification-service).

## Additional Information

- **Event-Driven Architecture:** With **Apache Kafka**, **TransactionService** leverages an **event-driven communication** model, making it scalable and loosely coupled with other services in the system.
This allows for efficient handling of events and smoother integration with future services.


- **Testing and Development:** The **H2 Database** supports fast development cycles, being used for in-memory testing during runtime.
For integration tests, **Testcontainers** provides robust and isolated environments through disposable containers, while **PostgreSQL** serves as the production-grade database.


- **Modularity and Scalability:** **TransactionService** is built to be modular and scalable, allowing easy expansion and integration with new features.
The microservice uses synchronous communication for interactions with **AccountService** and **UserService**, while employing **event-driven communication** via **Apache Kafka** for interaction with **NotificationService**.
This hybrid architecture ensures efficient management of distributed systems and offers flexibility for future enhancements.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
