package com.infy.resource;

import com.infy.domain.Transfer;
import com.infy.dto.TransferDto;
import com.infy.service.BillingFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferResourceTest {

    @Mock private BillingFacade billingFacade;
    @Mock private AsyncResponse asyncResponse;
    @Captor private ArgumentCaptor<WebApplicationException> exceptionCaptor;
    @Captor private ArgumentCaptor<Response> responseCaptor;
    @InjectMocks private TransferResource subject;

    @Test
    public void getAll() throws Exception {
        List<Transfer> transfers = Collections.emptyList();
        when(billingFacade.getTransfers()).thenReturn(transfers);


        subject.getAll(asyncResponse);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), transfers);
        assertEquals(response.getStatus(), OK.getStatusCode());
    }

    @Test
    public void getById_notFound() throws Exception {
        long id = 1L;
        when(billingFacade.getTransfer(id)).thenReturn(Optional.empty());

        subject.getById(asyncResponse, id);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), NOT_FOUND.getStatusCode());
    }

    @Test
    public void getById() throws Exception {
        long id = 1L;
        Transfer transfer = new Transfer().setId(id).setName("Test");
        when(billingFacade.getTransfer(id)).thenReturn(Optional.of(transfer));

        subject.getById(asyncResponse, id);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), transfer);
        assertEquals(response.getStatus(), OK.getStatusCode());
    }

    @Test
    public void create_empty() throws Exception {
        TransferDto dto = new TransferDto();

        subject.create(asyncResponse, dto);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }

    @Test
    public void create() throws Exception {
        TransferDto dto = new TransferDto().setAmount(10L).setConsumerId(1L).setSupplierId(2L);
        Transfer transfer = new Transfer().setName("Transfer");
        when(billingFacade.createTransfer(dto)).thenReturn(transfer);

        subject.create(asyncResponse, dto);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), transfer);
        assertEquals(response.getStatus(), CREATED.getStatusCode());
    }
}
