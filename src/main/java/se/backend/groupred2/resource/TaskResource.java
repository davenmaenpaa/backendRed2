package se.backend.groupred2.resource;

import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Task.TaskStatus;
import se.backend.groupred2.model.User;
import se.backend.groupred2.resource.filter.AuthBinding;
import se.backend.groupred2.service.IssueService;
import se.backend.groupred2.service.TaskService;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Singleton
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("tasks")
public final class TaskResource {
    private final TaskService taskService;
    private final IssueService issueService;
    private final Sse sse;
    private final SseBroadcaster broadcaster;

    public TaskResource(TaskService taskService, IssueService issueService, @Context final Sse sse) {
        this.taskService = taskService;
        this.issueService = issueService;
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
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
    @Path("{id}")
    public Response getTask(@PathParam("id") Long id) {
        return taskService.getTask(id)
                .map(Response::ok)
                .orElse(Response.status(NO_CONTENT))
                .build();
    }

    @GET
    @Path("events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToBroadcast(@Context SseEventSink eventSink) {
        this.broadcaster.register(eventSink);
    }

    @GET
    @Path("issues")
    public Response getAllTasksWithIssues(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("limit") @DefaultValue("10") int limit) {
        return Response.ok(issueService.getAllTasksWithIssues(page, limit)).build();
    }

    @GET
    @Path("description")
    public List<Task> getAllTasksByDescription(@QueryParam("desc") String description,
                                               @QueryParam("page") @DefaultValue("0") int page,
                                               @QueryParam("limit") @DefaultValue("10") int limit) {
        return taskService.getAllTasksByDescription(description, page, limit);
    }

    @AuthBinding
    @POST
    public Response createTask(Task task) {
        Task result = taskService.createTask(task);
		
        notifyListeners(task);

        return Response.status(CREATED).header("Location", "Tasks/" + result.getId()).build();
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
    @Path("{id}/adduser")
    public Response addUserToTask(@PathParam("id") Long id, User user) {
        return taskService.addUserToTask(id, user.getId())
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

    @PUT
    @Path("issues/{id}")
    public Issue update(@PathParam("id") Long id, Issue issue) {
        return issueService.update(id, issue);
    }

    @DELETE
    @Path("{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        return taskService.deleteTask(id)
                .map(task -> Response.status(NO_CONTENT))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    private void notifyListeners(Task task) {
        final OutboundSseEvent event = sse.newEventBuilder()
                .name("Task")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, task.toString())
                .build();

        broadcaster.broadcast(event);
    }
}






