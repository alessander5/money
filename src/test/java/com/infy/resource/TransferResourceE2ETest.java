package com.infy.resource;

import com.infy.domain.Account;
import com.infy.dto.TransferDto;
import io.restassured.http.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransferResourceE2ETest {

    @Test
    public void order1_getAll_empty() throws Exception {
        when().get("/transfers")
            .then()
            .statusCode(OK.getStatusCode())
            .body("$.size()", equalTo(0));
    }

    @Test
    public void order2_create_emptyError() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .body(new TransferDto())
            .when().post("/transfers")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", is(containsString("Fields should not be empty")));
    }

    @Test
    public void order3_create_noConsumerAccountError() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .body(new TransferDto().setConsumerId(1L).setSupplierId(1L).setAmount(100L))
            .when().post("/transfers")
            .then()
            .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
            .body("message", is(containsString("Could not found consumer by id: 1")));
    }

    @Test
    public void order4_create_success() throws Exception {
        Account consumer = given()
            .contentType(ContentType.JSON)
            .body(new Account().setName("acc1").setBalance(100L))
            .when().post("/accounts")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(Account.class);

        Account supplier = given()
            .contentType(ContentType.JSON)
            .body(new Account().setName("acc2").setBalance(200L))
            .when().post("/accounts")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().as(Account.class);

        given()
            .contentType(ContentType.JSON)
            .body(new TransferDto().setAmount(10L).setSupplierId(supplier.getId()).setConsumerId(consumer.getId()))
            .when().post("/transfers")
            .then()
            .statusCode(CREATED.getStatusCode());

        given().pathParam("id", supplier.getId())
            .when().get("/accounts/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .body("balance", equalTo(190));
        given().pathParam("id", consumer.getId())
            .when().get("/accounts/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .body("balance", equalTo(110));

        // clean up
        given().pathParam("id", supplier.getId())
            .contentType(ContentType.JSON)
            .when().delete("/accounts/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
        given().pathParam("id", consumer.getId())
            .contentType(ContentType.JSON)
            .when().delete("/accounts/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }
}
