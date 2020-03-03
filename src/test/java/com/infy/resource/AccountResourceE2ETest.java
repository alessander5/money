package com.infy.resource;

import com.infy.domain.Account;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountResourceE2ETest {

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void order1_getById_error() throws Exception {
        given().pathParam("id", "1")
            .when().get("/accounts/{id}")
            .then()
            .statusCode(NOT_FOUND.getStatusCode())
            .body("message", is(containsString("Account with id:'1' not found")));
    }

    @Test
    public void order2_getAll_empty() throws Exception {
        when().get("/accounts")
            .then()
            .statusCode(OK.getStatusCode())
            .body("$.size()", equalTo(0));
    }

    @Test
    public void order3_create_idError() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .body(new Account().setId(1L).setName("name").setBalance(100L))
            .when().post("/accounts")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", is(containsString("Id should be empty")));
    }

    @Test
    public void order4_update_idError() throws Exception {
        given().pathParam("id", "-1")
            .contentType(ContentType.JSON)
            .body(new Account().setName("name").setBalance(100L))
            .when().put("/accounts/{id}")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", is(containsString("Id should not be less than zero")));
    }

    @Test
    public void order5_delete_error() throws Exception {
        given().pathParam("id", "1")
            .contentType(ContentType.JSON)
            .when().delete("/accounts/{id}")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", is(containsString("Account with id:'1' not found")));
    }

    @Test
    public void order6_crud() throws Exception {
        Account account = given()
            .contentType(ContentType.JSON)
            .body(new Account().setName("name").setBalance(100L))
            .when().post("/accounts")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(Account.class);

        when().get("/accounts")
            .then()
            .statusCode(OK.getStatusCode())
            .body("$.size()", equalTo(1));

        given().pathParam("id", account.getId())
            .when().get("/accounts/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .body("id", equalTo(account.getId().intValue()),
                "name", equalTo(account.getName()),
                "balance", equalTo(account.getBalance().intValue()));

        given().pathParam("id", account.getId())
            .contentType(ContentType.JSON)
            .body(account.setName("nameUpdated"))
            .when().put("/accounts/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .body("name", equalTo("nameUpdated"));

        given().pathParam("id", account.getId())
            .contentType(ContentType.JSON)
            .when().delete("/accounts/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }
}