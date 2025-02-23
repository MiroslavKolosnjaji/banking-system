[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/K6MEbnQdqEgQE7qSJFetp9/QNGz9hXhAQUmKHzbFEFP4B/tree/main.svg?style=svg&circle-token=CCIPRJ_NfX7yqQvVMQGPf2ck9j1jR_54312f5715c93dd4c7a30f3f6ac6c0d0095649ee)](https://dl.circleci.com/status-badge/redirect/circleci/K6MEbnQdqEgQE7qSJFetp9/QNGz9hXhAQUmKHzbFEFP4B/tree/main)
[![codecov](https://codecov.io/gh/MiroslavKolosnjaji/notification-service/graph/badge.svg?token=pLcrvB5U7h)](https://codecov.io/gh/MiroslavKolosnjaji/notification-service)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
![GitHub forks](https://img.shields.io/github/forks/MiroslavKolosnjaji/notification-service)

# notification-service

Notification Service is a microservice within the Banking System project, responsible for sending notifications to users related to their banking transactions.
This service ensures that users are promptly informed about key account activities such as deposits, withdrawals, and transfers.
By leveraging an event-driven architecture using Apache Kafka, the Notification Service ensures efficient, scalable, and real-time communication with other services, providing users with timely and relevant transaction updates.

## Technologies

- **Spring Boot:** Core framework for building enterprise Java applications.
- **Spring Boot DevTools:** Tools for improving development efficiency with features like automatic restarts and live reload.
- **Spring Boot Validation:** Ensures data integrity with rule-based constraints.
- **Spring Data MongoDB:** Simplifies database interactions with MongoDB, providing repository support and seamless mapping of Java objects to MongoDB documents.
- **MapStruct:** Used for mapping between domain entities and DTOs.
- **Lombok:** Library for reducing boilerplate code in Java.
- **Thymeleaf:** A Java templating engine for server-side rendering in Spring Boot applications, enabling dynamic HTML generation while maintaining valid standalone templates.
- **Apache Kafka:** Event streaming platform used for communication between services in an event-driven architecture.
- **MongoDB:** NoSQL database used for scalable, high-performance storage of unstructured or semi-structured data.
- **JUnit5:** Testing framework for unit and integration testing in Java.
- **Mockito:** Framework for creating mock objects in automated testing.
- **GreenMail:** A test-friendly email server for Java that supports SMTP, IMAP, and POP3, enabling email testing in applications.
- **Testcontainers:** Library for running lightweight, disposable containers for database and service integration tests.
- **Docker:** A platform for building, distributing, and running applications in containers.


## Additional Information

- **Event-Driven Architecture:** The Notification Service uses **Apache Kafka** for **event-driven communication**, allowing it to handle notifications asynchronously.
This approach ensures scalability, flexibility, and loose coupling with other services within the banking system.


- **Modularity and Scalability:** The Notification Service is modular, enabling easy expansion and integration with new features.
Its event-driven design ensures scalability and seamless integration with other services as the system grows.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
