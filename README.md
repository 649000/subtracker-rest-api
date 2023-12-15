# Subtracker - A subscription service tracker API

## Overview 
As modern services shift towards subscription-based revenue models, managing diverse subscriptions becomes progressively intricate. Subtracker steps in to simplify this challenge, offering users a holistic view of their subscriptions. It provides comprehensive details such as costs, durations, and renewal dates in a streamlined manner.

This repository hosts the backend aspect, which comprises the REST API endpoints.


## Technologies Utilized
* Spring Boot: For robust and efficient API development.
* Spring Security: Ensuring secure authentication and access control.
* Spring Boot OAuth2 Resource Server: Enabling secure resource access via OAuth2.
* Lombok: Streamlining Java code through annotations to reduce boilerplate code.
* Maven: Facilitating project management and build automation.
* Firebase Auth & Firestore: Authentication service and database for secure user authentication and data storage.


## Installation and Setup Instructions
Firebase service account is required and must be set as an environment variable

Example of setting environment variable on macOS:
```
export GOOGLE_APPLICATION_CREDENTIAL=<JSON file goes here>
```

To run the service, navigate to the root of the project and execute the command

```
mvn spring-boot:run
```
## Endpoints
|          | localhost                                   | Railway                                                     |
|----------|---------------------------------------------|-------------------------------------------------------------|
| Open API | http://localhost:8080/swagger-ui/index.html | TBD |
| Actuator | http://localhost:8080/actuator              | TBD              |
| API      | http://localhost:8080/api                   | TBD              |
## Reflection

The core objective behind this project was to immerse myself in the Spring Boot framework's functionalities and capabilities. To achieve this, I embarked on building a robust RESTful API service, leveraging the Spring Boot framework in conjunction with complementary Spring components like Spring Security and Spring OAuth2 Resource Server.

Among the notable challenges encountered, one that stood out was the implementation of user authentication using Firebase Auth. Presented with the choice between crafting a custom authentication system or adopting an Identity as a Service (IDaaS) solution like AWS Cognito, Auth0, or Firebase Auth, I opted for the IDaaS approach. This decision stemmed from my belief that as developers, we should avoid redundant reinventions. Implementing a custom solution might inadvertently overlook critical security aspects, hence favoring a proven IDaaS solution felt more prudent.

In summary, this project not only deepened my familiarity with the Spring Boot framework but also underscored the significance of leveraging established identity management solutions to ensure robust security measures without compromising efficiency or reliability.

With Spring Security, all endpoints barring `/actuator` and `/swagger-ui/**` are secured and require a valid token provided by Firebase.
