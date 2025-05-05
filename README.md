# Subscription Tracker API Service
![Build](https://github.com/649000/subtracker-rest-api/actions/workflows/ci.yml/badge.svg)

![CodeQL](https://github.com/649000/subtracker-rest-api/actions/workflows/codeql.yml/badge.svg)

![Coverage](https://codecov.io/gh/649000/subtracker-rest-api/branch/develop/graph/badge.svg)

![SonarCloud Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=649000_subtracker-rest-api&metric=alert_status)

![Dependabot](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen)


## Overview 
SubTracker is a REST API service designed to help users manage their subscription-based services in one centralized location. With the growing number of subscription services in our daily lives, SubTracker enables users to track costs, durations, and renewal dates through a clean, secure API.

This repository contains the backend service that powers the SubTracker application, built with modern Java technologies and security best practices.

## Architecture

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│  Client App │────▶│  SubTracker API  │────▶│  Firestore  │
└─────────────┘     │  Spring Boot     │     └─────────────┘
        │           └──────────────────┘            ▲
        │                    │                      │
        └────────────────────▼──────────────────────┘
                      Firebase Auth
```


## Tech Stack

- **Spring Boot** - Framework for building production-ready applications
- **Spring Security** - Authentication and authorization framework
- **Spring OAuth2 Resource Server** - OAuth2 implementation for secure API access
- **Firebase Auth** - User authentication and token validation
- **Firestore** - NoSQL database for storing subscription data
- **Lombok** - Annotation-based Java boilerplate code reducer
- **Maven** - Dependency management and build automation
- **GitHub Actions** - CI/CD pipeline for automated testing and deployment


## Getting Started

### Prerequisites
- JDK 21 or higher
- Maven
- Firebase Project with service account

### Setup

1. Clone the repository:
   ```
   git clone https://github.com/649000/subtracker-rest-api.git
   cd subtracker-rest-api
   ```

2. Set up Firebase service account:
   ```bash
   # macOS/Linux
   export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json
   
   # Windows
   set GOOGLE_APPLICATION_CREDENTIALS=C:\path\to\service-account.json
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Endpoints

| Service   | Local                                     | Production                                               |
|-----------|-------------------------------------------|---------------------------------------------------------|
| API Base  | http://localhost:8080/api                 | https://subtracker-api.onrender.com/api                 |
| OpenAPI   | http://localhost:8080/swagger-ui/index.html | https://subtracker-api.onrender.com/swagger-ui/index.html |
| Actuator  | http://localhost:8080/actuator            | https://subtracker-api.onrender.com/actuator            |

**Note:** All API endpoints are secured and require a valid JWT access token from Firebase Auth. Only the `/actuator` and `/swagger-ui/**` endpoints are publicly accessible.


## Reflection

The core objective behind this project was to immerse myself in the Spring Boot framework's functionalities and capabilities. To achieve this, I embarked on building a robust RESTful API service, leveraging the Spring Boot framework in conjunction with complementary Spring components like Spring Security and Spring OAuth2 Resource Server.

Among the notable challenges encountered, one that stood out was the implementation of user authentication using Firebase Auth. Presented with the choice between crafting a custom authentication system or adopting an Identity as a Service (IDaaS) solution like AWS Cognito, Auth0, or Firebase Auth, I opted for the IDaaS approach. This decision stemmed from my belief that as developers, we should avoid redundant reinventions. Implementing a custom solution might inadvertently overlook critical security aspects, hence favoring a proven IDaaS solution felt more prudent.

In summary, this project not only deepened my familiarity with the Spring Boot framework but also underscored the significance of leveraging established identity management solutions to ensure robust security measures without compromising efficiency or reliability.

With Spring Security, all endpoints barring `/actuator` and `/swagger-ui/**` are secured and require a valid token provided by Firebase.
