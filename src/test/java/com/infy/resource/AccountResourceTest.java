package com.infy.resource;

import com.infy.domain.Account;
import com.infy.domain.Transfer;
import com.infy.dto.TransferDto;
import com.infy.service.BillingFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {

    @Mock private BillingFacade billingFacade;
    @Mock private AsyncResponse asyncResponse;
    @Captor private ArgumentCaptor<WebApplicationException> exceptionCaptor;
    @Captor private ArgumentCaptor<Response> responseCaptor;
    @InjectMocks private AccountResource subject;

    @Test
    public void getById_notFound() throws Exception {
        long id = 1L;
        when(billingFacade.getAccount(id)).thenReturn(Optional.empty());

        subject.getById(asyncResponse, id);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), NOT_FOUND.getStatusCode());
    }

    @Test
    public void getById() throws Exception {
        long id = 1L;
        Account account = new Account().setId(id).setName("Test");
        when(billingFacade.getAccount(id)).thenReturn(Optional.of(account));

        subject.getById(asyncResponse, id);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), account);
        assertEquals(response.getStatus(), OK.getStatusCode());
    }


    @Test
    public void getAll() throws Exception {
        List<Account> accounts = Collections.emptyList();
        when(billingFacade.getAllAccounts()).thenReturn(accounts);

        subject.getAll(asyncResponse);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), accounts);
        assertEquals(response.getStatus(), OK.getStatusCode());
    }

    @Test
    public void create_idNotEmpty() throws Exception {
        Account account = new Account().setId(1L);

        subject.create(asyncResponse, account);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }

    @Test
    public void create() throws Exception {
        Account account = new Account().setName("Name");
        when(billingFacade.createAccount(account)).thenReturn(account);

        subject.create(asyncResponse, account);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), account);
        assertEquals(response.getStatus(), CREATED.getStatusCode());
    }

    @Test
    public void update_idEmpty() throws Exception {
        long id = 0;
        Account account = new Account();

        subject.update(asyncResponse, id, account);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }

    @Test
    public void update() throws Exception {
        long id = 1L;
        Account account = new Account().setId(id).setName("Name");
        when(billingFacade.updateAccount(account)).thenReturn(account);

        subject.update(asyncResponse, id, account);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getEntity(), account);
        assertEquals(response.getStatus(), OK.getStatusCode());
    }

    @Test
    public void delete_idNotFound() throws Exception {
        long id = 1L;
        when(billingFacade.getAccount(id)).thenReturn(Optional.empty());

        subject.delete(asyncResponse, id);
        verify(asyncResponse).resume(exceptionCaptor.capture());
        final WebApplicationException response = exceptionCaptor.getValue();

        assertEquals(response.getResponse().getStatus(), BAD_REQUEST.getStatusCode());
    }

    @Test
    public void delete() throws Exception {
        long id = 1L;
        when(billingFacade.getAccount(id)).thenReturn(Optional.of(new Account()));
        when(billingFacade.removeAccount(id)).thenReturn(true);

        subject.delete(asyncResponse, id);
        verify(asyncResponse).resume(responseCaptor.capture());
        final Response response = responseCaptor.getValue();

        assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
    }
}
