package se.backend.groupred2.resource.mapper;

import se.backend.groupred2.service.exceptions.NoContentException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoContentMapper implements ExceptionMapper<NoContentException> {
    @Override
    public Response toResponse(NoContentException e) {
        return Response.noContent().build();
    }
}
