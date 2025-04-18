# 🏆 Rewards Program Application
This is a Spring Boot-based REST API application designed to manage customer transactions and calculate customer rewards points earned by the transaction history.

## 📌 Features
- Fetch transactions for a customer between two dates

- Calculate reward points monthly and in total

- Handle validation (e.g., invalid date ranges or customer IDs)

- Display results on a frontend table

- Return meaningful error messages from backend and show them in the UI


---

## ⚙️ Technologies Used



### Backend

- Java 8

- Spring Boot

- Spring MVC

- REST API



### Frontend

- HTML5

- Vanilla JavaScript

- CSS (optional for styling)



---



## 🛠️ How to Run



### Prerequisites



- Java 8

- Maven

- Any IDE (e.g., IntelliJ, Eclipse)


 ### Steps

1. Clone the repository:

   ```bash

   git clone https://github.com/Kameshraj05/RewardsApplication.git

   cd rewards-program

  ## Build and Run

    'mvn spring-boot:run'
    
## System Architecture
The application follows a layered architecture, with the following components:
- **Controller Layer:** Handles incoming HTTP requests and routes them to the appropriate service.
- **Service Layer:** Contains the business logic for calculating rewards points and retrieving transaction data.
- **Repository Layer:** Interacts with the database to persist and retrieve data.
- **Database:** Stores customer transaction data.

## Database Design
### Entities
**Transaction:**
- id (Primary Key)
-	customerId
-	transactionDate
-	purchaseAmount

## Table Schema
| Table       | Column          | Data Type             | Constraints             | Description                                                              |
| :---------- | :-------------- | :-------------------- | :---------------------- | :----------------------------------------------------------------------- |
| transaction | id            | VARCHAR(255)          | PRIMARY KEY             | Unique identifier for the transaction.                                   |
|             | customerId      | VARCHAR(255)          | NOT NULL                | ID of the customer who made the transaction.                           |
|             | transactionDate | TIMESTAMP             | NOT NULL                | Date and time of the transaction.                                        |
|             | purchaseAmount  | DECIMAL(10, 2)        | NOT NULL                | Amount of the purchase.                                                  |

## API Specifications
### Customer Purchase API
This API endpoint is responsible for recording customer purchase details, specifically transaction amounts and dates, into the database.
### API Description
1. Method:POST
2. Endpoint: /transaction
3. Content Type: application/json
### Request Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| purchaseAmount    | Double          | 1..1       | This value defines the amount spent by the customer on the purchase.        |
| transactionDate   | LocalDateTime   | 1..1       | This parameter denotes the date on which the customer made the purchase. |
### Request Sample Data
'{ 
"customerId": "CUST123", 
"transactionDate": "2024-07-28T10:00:00", 
"purchaseAmount": 125.50
 }
'
### Response Data
In the response of this API, the client will receive the HTTP status code along with its respective message in the response body as given below.
| Status Code | Status Description    | Message                                                                     |
| :---------- | :-------------------- | :-------------------------------------------------------------------------- |
| 201         | Created               | Transaction recorded successfully                                           |
| 400         | Bad Request           | Invalid transaction data: [Error message]                                   |
| 500         | Internal Server Error | An error occurred while recording transaction.                              |

### Customer Rewards Calculation API
This API allows for retrieving customer transaction history and calculating rewards points based on that history. It is designed to be used by front-end applications and other systems that need to access and display customer rewards data.
### API Description
1. Method:GET
2. Endpoint: /customer/{customerId}?fromDate={fromDate}&toDate={toDate}
3. Response Content Type: application/json
### Request Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| fromDate    | LocalDate          | 1..1       | The user input to provide the start date to fetch customer transactions.        |
| toDate   | LocalDate   | 1..1       | The user input to provide the end date to fetch customer transactions. |
### Request Sample Data
'Sample url: /customer/CUST123?fromDate=2024-01-01&toDate=2024-07-31'
### Response Parameters
| Parameter Name    | Data Type       | Occurrence | Description                                                                 |
| :---------------- | :-------------- | :--------- | :-------------------------------------------------------------------------- |
| customerId        | String          | 1..1       | Customer Id is the key for customer identification.                         |
| totalPoints    | int          | 1..1       | Total reward points earned by the customer from the transactions within the given date range.      |
| monthlyData   | Map   | 1..1       | An object where each key is a month (e.g., "JULY"), and the value is an object with "points" and "transactionCount". |
### Response Sample Data
'{ 
"customerId": "CUST123", 
"monthlyData": {
 "JULY": { 
"points": 90,
 "transactionCount": 1
 },
 "JUNE": {
 "points": 240,
 "transactionCount": 2
 }
 },
 "totalPoints": 330
 }
'
| Status Code | Status Description    | Message                                                                                                 |
| :---------- | :-------------------- | :------------------------------------------------------------------------------------------------------ |
| 200         | OK                    | The request was successful.                                                                             |
| 400         | Bad Request           | Invalid Customer Id, Invalid Date Inputs.                                                               |
| 404         | Not Found             | No transactions found for customerId: CUST123 within the specified date range:                           |
| 500         | Internal Server Error | An error occurred while fetching rewards! Please, try again                                               |
