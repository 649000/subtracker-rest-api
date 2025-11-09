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
   export GOOGLE_APPLICATION_CREDENTIALS_JSON='{"type":"service_account",...}'
   export SUBTRACKER_PROJECT_ID=<firebase_project_id>
   
   # Windows
   set GOOGLE_APPLICATION_CREDENTIALS_JSON={"type":"service_account",...}
   set SUBTRACKER_PROJECT_ID=<firebase_project_id>
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Endpoints

| Service   | Local                                     | Production  |
|-----------|-------------------------------------------|-------------|
| API Base  | http://localhost:8080/api                 | Not deployed |
| OpenAPI   | http://localhost:8080/swagger-ui/index.html | Not deployed |
| Actuator  | http://localhost:8080/actuator            | Not deployed |

**Note:** All API endpoints are secured and require a valid JWT access token from Firebase Auth. Only the `/actuator` and `/swagger-ui/**` endpoints are publicly accessible.

## Security Implementation

The API implements a token-based authentication system using Firebase Auth:

1. Client applications authenticate users through Firebase Auth
2. Firebase issues JWT tokens to authenticated users
3. These tokens are validated by Spring Security's OAuth2 Resource Server
4. Each API request must include a valid token in the Authorization header

## Deployment

This application includes Terraform configuration for deployment to AWS App Runner, but is not currently deployed to save on hosting costs. This is a personal project and does not require a production environment.

The infrastructure configuration can be found in the `terraform/` directory.

## Reflection

This project was built to deepen my understanding of Spring Boot and related technologies while solving a real-world problem. Instead of reinventing the wheel with custom authentication, I opted for Firebase Auth as an Identity-as-a-Service (IDaaS) solution to ensure robust security without compromising development time.

The project successfully demonstrates:
- Building RESTful APIs with Spring Boot
- Implementing industry-standard authentication with OAuth2
- Integration with cloud services (Firebase)
- Setting up proper CI/CD pipelines with test coverage
- Code quality monitoring through SonarCloud
- Infrastructure as Code with Terraform

## Performance Note

When deployed, the service would use AWS App Runner which may have different performance characteristics compared to local development environments. Initial requests may experience delays while the service initializes.
