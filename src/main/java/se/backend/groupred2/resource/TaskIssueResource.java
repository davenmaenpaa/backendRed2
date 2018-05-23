package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Task.TaskStatus;
import se.backend.groupred2.resource.filter.AuthBinding;
import se.backend.groupred2.service.IssueService;
import se.backend.groupred2.service.TaskService;

import javax.ws.rs.*;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("tasks")
public final class TaskIssueResource {
    private final IssueService issueService;
    private final TaskService taskService;

    public TaskIssueResource(IssueService issueService, TaskService taskService) {
        this.issueService = issueService;
        this.taskService = taskService;
    }

    @GET
    @Path("issues")
    public Response getAllTasksWithIssues(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("limit") @DefaultValue("10") int limit) {
        return Response.ok(issueService.getAllTasksWithIssues(page, limit)).build();
    }

    @POST
    @AuthBinding
    @Path("{id}/issues")
    public Response createIssue(@PathParam("id") Long taskId, Issue issue) {
        Issue result = issueService.createIssue(taskId, issue);

        taskService.updateStatus(taskId, new Task(TaskStatus.UNSTARTED));

        return Response.status(CREATED).header("Location", "Teams/Issues/" + result.getId()).build();
    }

    @PUT
    @Path("issues/{id}")
    public Issue update(@PathParam("id") Long id, Issue issue) {
        return issueService.update(id, issue);
    }
}