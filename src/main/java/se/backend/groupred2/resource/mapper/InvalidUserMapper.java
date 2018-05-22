package se.backend.groupred2.resource.mapper;

import se.backend.groupred2.service.exceptions.InvalidUserException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public final class InvalidUserMapper implements ExceptionMapper<InvalidUserException> {

    @Override
    public Response toResponse(InvalidUserException exception) {
        return Response.status(BAD_REQUEST).entity(singletonMap("error", exception.getMessage())).build();
    }
}