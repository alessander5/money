package com.infy.guice;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Provider
@Singleton
public class ThrowableMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", exception.getMessage());
        Response response = Response
            .status(exception instanceof WebApplicationException
                ? ((WebApplicationException) exception).getResponse().getStatus()
                : 500)
            .entity(error)
            .build();
        log.error(response.toString());
        return response;
    }
}
