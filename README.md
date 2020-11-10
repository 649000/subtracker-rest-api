## Subtracker

As many services are moving towards a subscription based revenue model, it becomes increasingly difficult for consumers to track the subscriptions one has signed up for. The aim of this application is to track subscriptions a user have, giving an overview on the subscription cost, subscription period and renewal date. 

This particular repository host the backend aspect which is the REST API endpoint.

#### Technologies
1. Spring Boot
   1. Spring Data
   2. Spring Security
2. Lombok
3. Maven
4. Crnk - JSON API specification
5. PostgreSQL
6. Firebase Auth
7. Docker


## Project Status

This project is currently in development. The backend development is almost done, basic CRUD functionality with User and Subscription model is present. User authenticaton is also managed by Firebase. 


## Installation and Setup Instructions

Clone the repository. 

To Run Test Suite:  

`mvn clean test`  

To Start Server:

`mvn spring-boot:run`  

API endpoint:

`localhost:8080/api`  

## Reflection

The purpose of this project was to familiarise with the Spring Boot framework. In order to do, I decided to build a RESTful API service based on the Spring Boot framework alongside with other Spring projects such as Spring Data and Spring Security. 

The challenging aspect of this project was the user authentication with Firebase Auth. When it came to user authentication, I had the option of rolling out my own implementation or using Identity as a Service product such as AWS Cognito, Auth0, or Firebase Auth. I have decided to go forth with using an Identity as a Service approach as I believe developers should avoid reinventing the wheel especially when it comes to the security aspects of a project. Rolling out my implementation would not be ideal as I might not cover all grounds which can potentially lead to security loopholes. 

Using Spring Security, I was able to secure all of the endpoints. All endpoints will require a valid token provided by Firebase before a call can be made. 

#### Tools used
1. Postman  - to test the endpoints
2. Intellij IDE
