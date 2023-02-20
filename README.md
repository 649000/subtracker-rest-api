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


## Project Status

|Feature|Status  |
|--|--|
|CRUD on models|Completed  |
|User authentication via Firebase Auth|Completed  |
|Display results in JSON API| Completed
| Deployed on Heroku| Completed


## Installation and Setup Instructions

Clone the repository. 

To Run Test Suite:  

`mvn clean test`  

To Start Server:

`mvn spring-boot:run`  

API endpoint:

`localhost:8080/api`  

## Reflection

The purpose of this project was to familiarise with the Spring Boot framework. In order to do so, I decided to build a RESTful API service based on the Spring Boot framework alongside with other Spring projects such as Spring Data and Spring Security. 

The challenging aspect of this project was the user authentication with Firebase Auth. When it came to user authentication, I had the option of rolling out my own implementation or using Identity as a Service (IDaaS) product such as AWS Cognito, Auth0, or Firebase Auth. I have decided to go forth with using an IDaaS approach as I believe developers should avoid reinventing the wheel. Rolling out my implementation would not be ideal as I might overlook certain security aspects.

Using Spring Security, I was able to secure all of the endpoints. All endpoints requires a valid token which is provided by Firebase.

#### Tools used
1. Postman  - to test the endpoints
2. Intellij IDE
