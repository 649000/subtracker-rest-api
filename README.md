## Subtracker

As services are moving towards a subscription based revenue model, it becomes increasingly difficult for consumers to track the subscriptions one has signed up for. The aim of this application is to track subscriptions a user have, giving an overview on the subscription cost, subscription period and renewal date. 

This particular repository host the backend aspect which is the REST API endpoint.

#### Technologies
1. Spring Boot
2. Spring Security
3. Spring Boot OAuth2 Resource Server
4. Lombok
5. Maven
6. Firebase Auth & Firestore


## Installation and Setup Instructions
Firebase service account is required and must be set as an environment varibale

Example on setting environment variable on macOS:
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

The purpose of this project was to familiarise with the Spring Boot framework. In order to do so, I decided to build a RESTful API service based on the Spring Boot framework alongside with other Spring projects such as Spring Security and Spring OAuth2 Resource Server. 

The challenging aspect of this project was the user authentication with Firebase Auth. When it came to user authentication, I had the option of rolling out my own implementation or using Identity as a Service (IDaaS) product such as AWS Cognito, Auth0, or Firebase Auth. I decided to go forth with using an IDaaS approach as I believe developers should avoid reinventing the wheel. Rolling out my implementation would not be ideal as I might overlook certain security aspects.

With Spring Security, all endpoints barring `/actuator` and `/swagger-ui/**` are secured and requires a valid token provided by Firebase.

#### Tools used
1. Postman  - to test the endpoints
2. Intellij IDE
