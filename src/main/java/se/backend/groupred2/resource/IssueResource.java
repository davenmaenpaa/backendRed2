package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.service.IssueService;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Produces(APPLICATION_JSON)
@Component
@Path("issues")
public final class IssueResource {
    private final IssueService service;

    public IssueResource(IssueService service) {
        this.service = service;
    }

    @GET
    public Iterable<Issue> getAllIssues(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("limit") @DefaultValue("20") int limit) {
        return service.getAllIssues(page, limit);
    }
}
