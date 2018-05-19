package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Task;
import se.backend.groupred2.model.User;
import se.backend.groupred2.service.IssueService;
import se.backend.groupred2.service.TaskService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("tasks")
public final class TaskResource {
    private final TaskService taskService;
    private final IssueService issueService;

    public TaskResource(TaskService taskService, IssueService issueService) {
        this.taskService = taskService;
        this.issueService = issueService;
    }

    @POST
    public Response createTask(Task task) {

        Task result = taskService.createTask(task);
        return Response.status(CREATED).header("Location", "Tasks/" + result.getId()).build();
    }

    @GET
    @Path("{id}")
    public Response getTask(@PathParam("id") Long id) {
        return taskService.getTask(id)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @GET
    public Response getAllTasks(@QueryParam("status") String status,
                                      @QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("limit") @DefaultValue("10") int limit) {
        if (status == null)
            return Response.ok(taskService.getAllTasks(page, limit)).build();

        return Response.ok(taskService.getAllTasksByStatus(status, page, limit)).build();
    }

    @GET
    @Path("team/{id}")
    public List<Task> getAllTasksByTeam(@PathParam("id") Long teamId) {
        return taskService.getAllTasksByTeamId(teamId);
    }

    @GET
    @Path("user/{id}")
    public List<Task> getAllTasksByUser(@PathParam("id") Long userId,
                                        @QueryParam("page") @DefaultValue("0") int page ,
                                        @QueryParam("limit") @DefaultValue("10") int limit) {
        return taskService.getAllTasksByUserId(userId, page, limit);
    }

    @GET
    @Path("description")
    public List<Task> getAllTasksByDescription(@QueryParam("desc") String description,
                                               @QueryParam("page") @DefaultValue("0") int page ,
                                               @QueryParam("limit") @DefaultValue("10") int limit) {
        return taskService.getAllTasksByDescription(description, page, limit);
    }

    @DELETE
    @Path("{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        return taskService.deleteTask(id)
                .map(task -> Response.status(NO_CONTENT))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("{id}/adduser")
    public Response assignTaskToUser(@PathParam("id") Long id, User user) {
        return taskService.assignTaskToUser(id, user.getId())
                .map(t -> Response.status(NO_CONTENT))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response updateTask(@PathParam("id") Long id, Task task) {
        return taskService.updateStatus(id, task)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

}






