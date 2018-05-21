package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.User;
import se.backend.groupred2.resource.filter.AuthBinding;
import se.backend.groupred2.service.TaskService;
import se.backend.groupred2.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("users")
public final class UserResource {
    private final UserService userService;
    private final TaskService taskService;

    public UserResource(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GET
    public Response getUsers(
            @QueryParam("usernumber") @DefaultValue("0") long usernumber,
            @QueryParam("userName") @DefaultValue("0") String userName,
            @QueryParam("firstName") @DefaultValue("0") String firstName,
            @QueryParam("lastName") @DefaultValue("0") String lastName,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("limit") @DefaultValue("10") int limit) {

        if (usernumber == 0 && userName.equals("0") && firstName.equals("0") && lastName.equals("0"))
            return Response.ok(userService.getAllUsers(page, limit)).build();

        return Response.ok(userService.getUserByUserNamefirstNameLastName(usernumber, userName, firstName, lastName)).build();
    }

    @GET
    @Path("{id}/tasks")
    public List<Task> getAllTasksByUser(@PathParam("id") Long userId,
                                        @QueryParam("page") @DefaultValue("0") int page ,
                                        @QueryParam("limit") @DefaultValue("10") int limit) {
        return taskService.getAllTasksByUserId(userId, page, limit);
    }

    @POST
    @AuthBinding
    public Response createUser(User user) {
        User result = userService.createUser(user);

        return Response.status(CREATED).header("Location", "Users/" + result.getId()).build();
    }

    @PUT
    @Path("update")
    public Response update(User user) {
        return userService.update(user)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("deactivate")
    public Response deActivate(User user) {
        return userService.deActivate(user)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }
}