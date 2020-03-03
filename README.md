# Money transfer Rest API

A simple Java RESTful API for money transfers between users accounts

### Technologies
- JAX-RS API
- Guice
- Hibernate
- H2 in memory database
- Slf4j
- Jetty & Jersey
- Rest Assured


### How to run
```sh
mvn jetty:run
```

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /accounts | get all accounts |
| GET | /accounts/{id} | get account by id |
| POST | /accounts | create a new account |
| PUT | /accounts/{id} | update account |
| DELETE | /accounts/{id} | remove account |
| GET | /transfers | get all transfers |
| GET | /transfers/{id} | get transfer by id |
| POST | /transfers | perform transfer between 2 accounts |

### Http Status
- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 404 Not Found
- 500 Internal Server Error


----

### Create accounts:
```sh

POST
http://localhost:8080/accounts
{
  "name": "test1",
  "balance": 100
}

POST
http://localhost:8080/accounts
{
  "name": "test2",
  "balance": 200
}

GET
http://localhost:8080/accounts

```
##### Create transfer:

```sh

POST
http://localhost:8080/transfers

{
  "consumerId": 1,
  "supplierId": 2,
  "amount": 10
}

GET
http://localhost:8080/transfers

GET
http://localhost:8080/accounts

```

