package se.backend.groupred2.resource.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
@PreMatching
public final class PreMatchingFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getMethod().equals("POST")) {

            if (requestContext.getHeaderString("auth-token") != null) {
                String token = requestContext.getHeaderString("auth-token");

                System.out.println(token);

                if(!token.equals("password"))
                    requestContext.abortWith(Response.status(UNAUTHORIZED)
                            .entity("wrong password.")
                            .build());
            } else {
                requestContext.abortWith(Response.status(UNAUTHORIZED)
                        .entity("Client-Name header must be defined.")
                        .build());
            }
        }
    }
}
