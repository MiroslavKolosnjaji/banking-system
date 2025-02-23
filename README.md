![WIP](https://img.shields.io/badge/WIP-Work%20in%20Progress-yellow?logo=github)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# Banking System

## Table of Contents

- [Project Introduction](#project-introduction)
- [Prerequisites](#prerequisites)
- [Installation Guide](#installation-guide)
- [Notes](#notes)
- [Architecture Diagrams](#architecture-diagrams)
- [Usage Instructions](#usage-instructions)
- [Api Documentation](#api-documentation)
- [Microservices](#microservices)
- [License](#license)

## Project Introduction

This project simulates a **banking system**, with a primary focus on demonstrating **microservice architecture** in a real-world environment.

It adopts a **hybrid architecture**, where **WebClient** is used for **synchronous** interactions, while **Apache Kafka** enables **event-driven** processing.
The system consists of multiple independent microservices, each handling a specific domain, and follows a **stateless design**.
The project is fully containerized using **Docker**, ensuring seamless setup and deployment.

## Prerequisites

If you want to run this project locally, ensure that you have the following installed and set up on your machine:

1. **Docker Desktop**
   Docker is required to run the services in containers. You can download and install Docker Desktop
   from [Docker's official website](https://www.docker.com/products/docker-desktop/).


2. **Docker Compose**
   Docker Compose is required to run multi-container Docker applications. If you're using Docker Desktop, Docker Compose
   should be pre-installed.
   You can check if Docker Compose is installed by running:

   ```bash
   docker-compose --version
   ```

3. **Java 17**

   This project uses **Java 17**. Make sure you have JDK 17 installed.


4. **Enable email notifications**

    To fully utilize this project, you need to configure email notifications.

   - The project includes a preconfigured email setup for Gmail. If you're using Gmail, you only need to generate an app password.
     You can find instructions on how to do this in [this guide](https://www.youtube.com/watch?v=ZfEK3WP73eY).
    - If you're using another email provider (e.g., Outlook, Yahoo, etc.), refer to your provider's documentation for SMTP configuration and app password generation.
    - Once you have your credentials, update the ```docker-compose``` file (lines 208 and 209) with your email and password:
   
   ```yaml
    - YOUR_USERNAME=yourEmail@gmail.com
    - YOUR_PASSWORD=abcd efgh ijkl mnop 
   ```
   - If you are using an email provider other than Gmail, you will also need to update the email configuration settings in the NotificationService [application.properties](NotificationService/src/main/resources/application.properties) file:
    ```properties
        email.host=smtp.yourprovider.com
        email.port=yourPort
    ```
   
   - **NOTES:**
     - **Ensure that you use a valid and accessible email address for configuration, as this project actively sends emails.**
     - **Avoid using temporary or incorrect email addresses to prevent sending notifications to unintended recipients.**
     - **If testing, consider using a dedicated testing email account to avoid disruptions to your primary inbox.**
     - **To view email examples, refer to the API Documentation section under [Email Notifications](#email-notifications)**


5. **Postman** (Optional)

   If you'd like to test the APIs, I recommend using **Postman** to easily interact with the services.
   You can download Postman from the [Postman's official website](https://www.postman.com/downloads/).
   
   - **Postman Web** Alternatively, you can use [Postman Web](https://www.postman.com/) directly in your browser. You'll need to sign up for a free account, and you can then import the provided Postman collections to interact with the APIs without downloading anything.
   
   - The collections are already prepared and available for you to test the API endpoints without manually setting them up.
   Please refer to [API Documentation](#api-documentation)


6. **Minimum system resources**
    - **RAM**: 6 GB (Recommended: 8 GB)
    - **Disc Space:** 10 GB free

## Installation Guide

To deploy the project to your local computer, follow these steps:

1. **Clone project**

   Clone this repository to your local machine:

   ```bash
    git clone https://github.com/miroslavkolosnjaji/banking-system.git
   ```

2. **Navigate to the project folder**

   Change your directory to the project folder.

   ```bash
    cd banking-system
   ```

3. **Start the services with Docker Compose**

   To start all the services, use ```docker-compose up``` command. This will build and start all the necessary
   contaners for the project.

   ```bash
    docker-compose up --build
   ```

The ```--build``` flag ensures that Docker Compose rebuilds the images if there are any changes in the Dockerfile or
application code.

4. **Access the services**

   Once the containers are up and running, you can access the services at the following URL:
    - API Gateway: ```http://localhost:8080```
    - Eureka Server: ```http://localhost:8761```
    - Other services will be available via API Gateway. **Please check [Usage Instructions](#usage-instructions)**



## Notes

- Make sure that Docker Desktop is running before you attempt to start the services with Docker Compose.


- The first time you build and run the project, it may take approx **10 minutes** to download dependencies and set up
  services.


- The project consumes from **3.5** to **4.5 GB** of **RAM** while running in docker.


- If you want to stop all running services, use the following command:

   ```bash
  docker-compose down
   ```
- **Note: This project is still under development, and many functionalities have not yet been implemented. Some features may be incomplete or not yet functional.**


## Architecture Diagrams

- **Microservice Architecture:** [C4 Container Diagram](Docs/diagrams/container/Banking%20System%20Container%20Diagram.png)


- **ER Diagrams:** (This section also includes other Table Schema Diagrams for services with single tables, providing a visual overview of their schema.)
    - [Account Service table schema diagram](Docs/diagrams/er/Account%20Service%20Table%20Schema%20Diagram.png)
    - [User Service ER diagram](Docs/diagrams/er/User%20Service%20ER%20Diagram.png)
    - [Transaction Service table schema diagram](Docs/diagrams/er/Transaction%20Service%20Table%20Schema%20Diagram.png)
  

- **Document Structure:**
    - Notification Service (MongoDb JSON Example)
    ```json
    {
     "id": "6786b8309a8fc7349888a192",
     "messageId": "91188dfb-4723-43cd-bd0e-c794710017d4",
     "transactionId": 3,
     "userId": 1,
     "accountNumber": "96895****64",
     "amount": 56000.00,
     "balance": 1680000.00,
     "transactionType": "DEPOSIT",
     "description": "Transaction is successfully performed.",
     "status": "SUCCESS",
     "createdAt": "2025-01-14T19:17:04.586Z",
    }
     ```

## Usage Instructions

### Checking if Services are Running

- Open Docker Desktop and make sure all containers are up and running.
  You should see containers for:

  - `api-gateway`
  - `eureka-server`
  - `account-service-1`
  - `account-service-2`
  - `account-db`
  - `user-service-1`
  - `user-service-2`
  - `user-db`
  - `transaction-service-1`
  - `transaction-service-2`
  - `transaction-db`
  - `notification-service-1`
  - `notification-service-2`
  - `notification-db`
  - `kafka`

### Waiting for API Gateway and Services Registration
- **API Gateway** might take a few moments to fully register all services with **Eureka Discovery** after starting.
During this time, it might not be able to route requests properly.


- Please wait until all services appear in the Eureka Dashboard (accessible at [http://localhost:8761](http://localhost:8761)) before making any API calls via the API Gateway.


### Testing 


- Once the setup is complete, you can begin testing the project. 
A Postman collection with pre-configured API tests is provided and can be found in the [API Documentation](#api-documentation) section.
If you're not using Postman, you can still refer to the collection file as a reference for the API endpoints.


## API Documentation

For API documentation, including all endpoints, request/response examples, and descriptions, please refer to the [**Postman collection**](https://documenter.getpostman.com/view/33317443/2sAYX5KNF7).
This collection contains API requests for managing users, accounts, and transactions in the banking system.


### Email Notifications

The system sends email notifications for important account activities. Below are examples of email notifications users receive:

-   [Deposit Confirmation](Docs/examples/emails/transaction/deposit_confirmation.png)
-   [Withdrawal Confirmation](Docs/examples/emails/transaction/withdrawal_confirmation.png)
-   [Transfer Confirmation](Docs/examples/emails/transaction/transfer_confirmation.png)

## Microservices

This project consists of the following microservices:

-   [Account Service](AccountService/README.md) 
-   [Api Gateway](ApiGateway/README.md) 
-   [Eureka Discovery Service](DiscoveryServer/README.md) 
-   [Notification Service](NotificationService/README.md) 
-   [Transaction Service](TransactionService/README.md) 
-   [User Service](UserService/README.md) 

**Click on the links above to explore the individual service documentation.**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.