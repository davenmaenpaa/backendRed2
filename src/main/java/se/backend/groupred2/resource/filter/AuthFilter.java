package se.backend.groupred2.resource.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@AuthBinding
@Provider
@Priority(Priorities.AUTHENTICATION)
public final class AuthFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            if (requestContext.getHeaderString(HttpHeaders.AUTHORIZATION) == null) {
                requestContext.abortWith(Response.status(UNAUTHORIZED)
                        .build());
            } else {

                String token = authorizationHeader
                        .substring(AUTHENTICATION_SCHEME.length()).trim();

                if(!token.equals("password"))
                    requestContext.abortWith(Response.status(UNAUTHORIZED)
                            .build());
            }
        }
}
