package se.backend.groupred2.resource.mapper;

import se.backend.groupred2.service.exceptions.BadRequestException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException e) {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
