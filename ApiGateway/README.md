[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

# API Gateway

The API Gateway is a centralized entry point for all microservices in this architecture.
It provides a **central** access point, routes requests to the appropriate microservices, and manages the flow of data throughout the system.

## Features

- **Routing:** The API Gateway receives all incoming HTTP requests and routes them to the appropriate microservices based on the URL path, HTTP method, and other parameters.
- **Load Balancing:** Distributes load among multiple instances of microservices, improving system performance and availability.


## Technologies

- **Spring Cloud Gateway:** is an API gateway that provides dynamic routing, load balancing, and security for microservices.
- **Eureka Discovery Server:** Used for service discovery.
  The API Gateway connects to Eureka to dynamically discover available microservices and route requests to the correct instance.