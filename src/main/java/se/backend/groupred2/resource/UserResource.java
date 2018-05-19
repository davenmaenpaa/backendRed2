package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.User;
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
    private final UserService service;

    public UserResource(UserService service) {
        this.service = service;
    }

    @POST
    public Response createUser(User user) {
        User result = service.createUser(user);
        return Response.status(CREATED).header("Location", "Users/" + result.getId()).build();
    }

    @PUT
    @Path("update")
    public Response update(User user) {

        return service.update(user)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("deactivate")
    public Response deActivate(User user) {

        return service.deActivate(user)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
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
            return Response.ok(service.getAllUsers(page, limit)).build();

        return Response.ok(service.getUserByUserNamefirstNameLastName(usernumber, userName, firstName, lastName)).build();
    }

    @GET
    @Path("getByTeamId/{id}")
    public List<User> getAllUserByTeamId(@PathParam("id") Long teamId) {
        return service.getALLUserByteamId(teamId);
    }
}