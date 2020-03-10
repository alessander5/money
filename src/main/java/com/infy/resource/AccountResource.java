package com.infy.resource;

import com.infy.domain.Account;
import com.infy.service.BillingFacade;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final BillingFacade billingFacade;

    @Inject
    public AccountResource(BillingFacade billingFacade) {
        this.billingFacade = billingFacade;
    }

    @GET
    @Path("/{id}")
    public void getById(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id) {
        Optional<Account> account = billingFacade.getAccount(id);
        if(!account.isPresent()) {
            asyncResponse.resume(new WebApplicationException("Account with id:'" + id + "' not found ", NOT_FOUND));
        } else {
            asyncResponse.resume(ok(account.get()).build());
        }
    }

    @GET
    public void getAll(@Suspended AsyncResponse asyncResponse) {
        asyncResponse.resume(ok(billingFacade.getAllAccounts()).build());
    }

    @POST
    public void create(@Suspended AsyncResponse asyncResponse, Account account) {
        if (account.getId() != null) {
            asyncResponse.resume(new WebApplicationException("Id should be empty", BAD_REQUEST));
        } else {
            asyncResponse.resume(status(CREATED).entity(billingFacade.createAccount(account)).build());
        }
    }

    @PUT
    @Path("/{id}")
    public void update(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id, Account account) {
        if(id <= 0) {
            asyncResponse.resume(new WebApplicationException("Id should not be less than zero", BAD_REQUEST));
        } else {
            asyncResponse.resume(ok(billingFacade.updateAccount(account.setId(id))).build());
        }
    }

    @DELETE
    @Path("/{id}")
    public void delete(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id) {
        Optional<Account> account = billingFacade.getAccount(id);
        if(!account.isPresent()) {
            asyncResponse.resume(new WebApplicationException("Account with id:'" + id + "' not found ", BAD_REQUEST));
        } else {
            Response.ResponseBuilder builder = billingFacade.removeAccount(id)
                ? Response.status(NO_CONTENT)
                : Response.status(NOT_FOUND);
            asyncResponse.resume(builder.build());
        }
    }
}
