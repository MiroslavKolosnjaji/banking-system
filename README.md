# Banking System  (Microservice Architecture DEMO)

## Table of Contents

- [Project Introduction](#project-introduction)
- [Prerequisites](#prerequisites)
- [Accessing the Project](#accessing-the-project)
- [Installation Guide](#installation-guide)
- [Notes](#notes)
- [Architecture Diagrams](#architecture-diagrams)
- [Usage Instructions](#usage-instructions)
- [Api Documentation](#api-documentation)
- [Microservices](#microservices)
- [License](#license)

## Project Introduction

This project is primarily intended to demonstrate a **microservice architecture**, focusing on both **synchronous** and **asynchronous** communication.

While the goal is not to build a real banking system, the project showcases how microservices can operate in a real-world environment.
It adopts a **hybrid architecture**, where **synchronous** communication is handled through **WebClient** and **asynchronous** communication utilizes an **event-driven architecture** powered by **Apache Kafka**.
This approach enables greater flexibility and scalability in system design and efficient event processing between services.

## Prerequisites

Before you begin, ensure that you have the following installed and set up on your machine:

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


4. **Postman** (Optional)

   If you'd like to test the APIs, I recommend using **Postman** to easily interact with the services.
   You can download Postman from the [Postman's official website](https://www.postman.com/downloads/).
   
   - **Postman Web** Alternatively, you can use [Postman Web](https://www.postman.com/) directly in your browser. You'll need to sign up for a free account, and you can then import the provided Postman collections to interact with the APIs without downloading anything.
   
   - The collections are already prepared and available for you to test the API endpoints without manually setting them up.
   Please refer to [API Documentation](#api-documentation)


5. **Minimum system resources**
    - **RAM**: 6 GB (Recommended: 8 GB)
    - **Disc Space:** 10 GB free

## Accessing the Project

**Since this is a private project, cloning the repository directly via Git is not possible.
However, you can download the project as ZIP file and extract it to your desired folder.
Once the files are extracted, you can start the services by running Docker Compose from that folder.**

**Steps:**
1. **Download the ZIP file of the project.**
2. **Extract the contents to a folder of your choice.**
3. **Open a terminal/command prompt and follow instructions from [step 2 in Installation guide.](#installation-guide)**


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