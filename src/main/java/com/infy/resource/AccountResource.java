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

import static java.util.concurrent.CompletableFuture.supplyAsync;
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
            throw new WebApplicationException("Account with id:'" + id + "' not found ", NOT_FOUND);
        } else {
            asyncResponse.resume(ok(account.get()).build());
        }
    }

    @GET
    public void getAll(@Suspended AsyncResponse asyncResponse) {
        supplyAsync(billingFacade::getAllAccounts)
            .thenApply(accounts -> asyncResponse.resume(ok(accounts).build()))
            .exceptionally(asyncResponse::resume);
    }

    @POST
    public void create(@Suspended AsyncResponse asyncResponse, Account account) {
        if (account.getId() != null) {
            asyncResponse.resume(new WebApplicationException("Id should be empty", BAD_REQUEST));
        } else {
            supplyAsync(() -> billingFacade.createAccount(account))
                .thenApply(newAccount -> asyncResponse.resume(status(CREATED).entity(newAccount).build()))
                .exceptionally(asyncResponse::resume);
        }
    }

    @PUT
    @Path("/{id}")
    public void update(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id, Account account) {
        if(id <= 0) {
            asyncResponse.resume(new WebApplicationException("Id should not be less than zero", BAD_REQUEST));
        } else {
            supplyAsync(() -> billingFacade.updateAccount(account.setId(id)))
                .thenApply(updatedAccount -> asyncResponse.resume(ok(updatedAccount).build()))
                .exceptionally(asyncResponse::resume);
        }
    }

    @DELETE
    @Path("/{id}")
    public void delete(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id) {
        Optional<Account> account = billingFacade.getAccount(id);
        if(!account.isPresent()) {
            throw new WebApplicationException("Account with id:'" + id + "' not found ", BAD_REQUEST);
        } else {
            supplyAsync(() -> billingFacade.removeAccount(id))
                .thenApply(isDeleted -> isDeleted
                        ? Response.status(NO_CONTENT).build()
                        : Response.status(NOT_FOUND).build())
                .thenApply(asyncResponse::resume)
                .exceptionally(asyncResponse::resume);
        }
    }
}
