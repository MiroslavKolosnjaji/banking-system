# account-service
[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/K6MEbnQdqEgQE7qSJFetp9/2mk49W7yp9txo6zdWqFM2B/tree/main.svg?style=svg&circle-token=CCIPRJ_XacLDtCvpvLDY58fc3ofY4_6850bbd2aecd624a4f3ef6c99cbc36536f1f5448)](https://dl.circleci.com/status-badge/redirect/circleci/K6MEbnQdqEgQE7qSJFetp9/2mk49W7yp9txo6zdWqFM2B/tree/main)
[![codecov](https://codecov.io/gh/MiroslavKolosnjaji/account-service/graph/badge.svg?token=aiLG2d5f7S)](https://codecov.io/gh/MiroslavKolosnjaji/account-service)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
![GitHub forks](https://img.shields.io/github/forks/MiroslavKolosnjaji/account-service)

### Account Service - Bank Account Management Microservice

## Description
Account Service is a microservice responsible for managing bank accounts within the Banking System project.
It handles core banking operations such as creating, updating, and maintaining accounts, as well as processing financial transactions like deposits, withdrawals, and transfers.


 ## Key Features:

- **Account Creation:** Enables the creation of new bank accounts, which are automatically associated with users.


- **Deposits & Withdrawals:** Supports depositing funds into accounts and withdrawing funds, with checks to ensure sufficient balance.


- **Account Transfer:** Facilitates transferring funds between accounts, ensuring proper validation of the accounts and available balance.


- **Account Status Update:** Allows the modification of an account's status (e.g., active, frozen).


- **Account Updates:** Facilitates updates to account details, including account type or user-associated information.


## Technologies

- **Spring Boot:** Core framework for building enterprise Java applications.
- **Spring Boot DevTools:** Tools for improving development efficiency with features like automatic restarts and live reload.
- **Spring Boot Validation:** Ensures that the data being processed adheres to specified rules and constraints.
- **Spring JPA:**  Provides support for java persistence API to manage database operations.
- **MapStruct:**  Used for mapping between domain entities and DTOs.
- **Lombok:** Library for reducing boilerplate code in Java.
- **PostgreSQL:** Relational database management system used for production-grade storage.
- **Flyway:**  Database migration tool for version-controlled schema changes.
- **H2 Database:** In-memory relational database for development and testing purposes.
- **JUnit5:** Testing framework for unit and integration testing in Java.
- **Mockito:** Framework for creating mock objects in automated testing.
- **Wiremock:** A flexible mocking tool for simulating HTTP-based APIs, used to mock external service responses in integration tests.
- **Testcontainers:** Library for running disposable containers in integration tests with databases and services.
- **Docker:** A platform for building, distributing, and running applications in containers.


## Additional Information

- **Testing and Development:** The **H2 Database** supports fast development cycles, being used for in-memory testing during runtime.
  For integration tests, **Testcontainers** provides robust and isolated environments through disposable containers, while **PostgreSQL** serves as the production-grade database.


- **Modularity and Scalability:** The Account Service is designed to be modular and scalable, enabling easy expansion and integration with additional features.
The microservice architecture ensures the system can handle future enhancements and complex banking operations efficiently.


- **Code Coverage Exclusions:** DTO, domain, and exception classes are excluded from code coverage as they primarily serve as data carriers or represent specific error states.
Field validations in DTOs are indirectly tested through integration tests for controllers, while exception classes typically do not require dedicated tests.


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
