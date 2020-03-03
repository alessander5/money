package com.infy.resource;

import com.infy.domain.Transfer;
import com.infy.dto.TransferDto;
import com.infy.service.BillingFacade;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import java.util.Optional;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

    private final BillingFacade billingFacade;

    @Inject
    public TransferResource(BillingFacade billingFacade) {
        this.billingFacade = billingFacade;
    }

    @GET
    public void getAll(@Suspended AsyncResponse asyncResponse) {
        supplyAsync(billingFacade::getTransfers)
            .thenApply(transfers -> asyncResponse.resume(ok(transfers).build()))
            .exceptionally(asyncResponse::resume);
    }

    @GET
    @Path("/{id}")
    public void getById(@Suspended AsyncResponse asyncResponse, @PathParam("id") long id) {
        Optional<Transfer> transfer = billingFacade.getTransfer(id);
        if(!transfer.isPresent()) {
            throw new WebApplicationException("Transfer with id:'" + id + "' not found ", NOT_FOUND);
        } else {
            asyncResponse.resume(ok(transfer.get()).build());
        }
    }

    @POST
    public void create(@Suspended AsyncResponse asyncResponse, TransferDto transferDto) {
        if (transferDto.getConsumerId() == null
            || transferDto.getSupplierId() == null
            || transferDto.getAmount() == null) {
            asyncResponse.resume(new WebApplicationException("Fields should not be empty", BAD_REQUEST));
        } else {
            supplyAsync(() -> billingFacade.createTransfer(transferDto))
                .thenApply(transfer -> asyncResponse.resume(status(CREATED).entity(transfer).build()))
                .exceptionally(asyncResponse::resume);
        }
    }

}
