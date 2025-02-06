
[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/K6MEbnQdqEgQE7qSJFetp9/KP4TqAANkAUwSBnDs9CrtU/tree/main.svg?style=svg&circle-token=CCIPRJ_85JGuaaWrhs28YyLo1gU5V_4d2dc4ea894063a299ae1e981e1e8bda1dd0cc25)](https://undefined/status-badge/redirect/circleci/K6MEbnQdqEgQE7qSJFetp9/KP4TqAANkAUwSBnDs9CrtU/tree/main)
[![codecov](https://codecov.io/gh/MiroslavKolosnjaji/user-service/graph/badge.svg?token=KGN53viBZH)](https://codecov.io/gh/MiroslavKolosnjaji/user-service)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
![GitHub forks](https://img.shields.io/github/forks/MiroslavKolosnjaji/user-service)

# UserService

UserService is a microservice responsible for managing users and their roles within a larger banking system project.
It uses synchronous communication to interact with other microservices, ensuring seamless user management within the system.

## Technologies

- **Spring Boot:** Core framework for building enterprise Java applications.
- **Spring Boot DevTools:** Tools for improving development efficiency with features like automatic restarts and live reload.
- **Spring Boot Validation:** Ensures that the data being processed adheres to specified rules and constraints.
- **Spring Crypto:** Provides cryptographic utilities for securing sensitive data, including encryption, decryption, hashing, and key management.
- **Spring JPA:**  Provides support for java persistence API to manage database operations.
- **Spring WebFlux:** Framework for building reactive web applications, used here in a synchronous manner for its lightweight architecture.
- **MapStruct:**  Used for mapping between domain entities and DTOs.
- **Lombok:** Library for reducing boilerplate code in Java.
- **PostgreSQL:** Relational database management system used for production-grade storage.
- **Flyway:**  Database migration tool for version-controlled schema changes.
- **H2 Database:** In-memory relational database for development and testing purposes.
- **JUnit5:** Testing framework for unit and integration testing in Java.
- **Mockito:** Framework for creating mock objects in automated testing.
- **Wiremock:** A flexible mocking tool for simulating HTTP-based APIs, used to mock external service responses in integration tests.
- **MockWebServer:** Lightweight server for testing HTTP clients, primarily used with WebClient.
- **Testcontainers:** Library for running disposable containers in integration tests with databases and services.
- **Docker:** A platform for building, distributing, and running applications in containers.

## Service Communication
When a new user is registered, **UserService** sends a request to **AccountService** to create a corresponding account.

For more information on **AccountService**, visit the [account-service repository](https://github.com/MiroslavKolosnjaji/account-service).

## Additional Information

- **Testing and Development:**  H2 Database ensures fast development cycles and is used for in-memory testing when the application is running.
For integration tests, Testcontainers provides robust and isolated environments by running disposable containers, while PostgreSQL serves as the production-grade database.


- **Modularity and Scalability:** The UserService is designed to be modular and scalable, ensuring seamless integration with other services.
It currently communicates synchronously with AccountService, but the architecture allows for future extensions, including event-driven communication if needed.


- **Code coverage exclusions:** Certain classes, such as DTOs, domain models, and exception handlers, are excluded from code coverage because they primarily serve as simple data carriers or manage specific error states.
These classes typically don't contain business logic that requires dedicated tests. Field validations in DTOs are indirectly covered through the integration tests for controllers, where the input data is validated during actual interactions with the system.
Additionally, exception classes are generally not tested directly since their behavior is triggered and validated within the flow of the application and service-level logic


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
