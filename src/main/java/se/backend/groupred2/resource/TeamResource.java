package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Team;
import se.backend.groupred2.model.User;
import se.backend.groupred2.resource.filter.AuthBinding;
import se.backend.groupred2.service.TaskService;
import se.backend.groupred2.service.TeamService;
import se.backend.groupred2.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("teams")
public final class TeamResource {
    private final TeamService teamService;
    private final UserService userService;
    private final TaskService taskService;

    public TeamResource(TeamService teamService, UserService userService, TaskService taskService) {
        this.teamService = teamService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @GET
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GET
    @Path("{id}")
    public Response getTeam(@PathParam("id") Long id) {
        return teamService.getTeam(id)
                .map(u -> Response.status(OK))
                .orElse(Response.status((NO_CONTENT)))
                .build();
    }

    @GET
    @Path("{id}/users")
    public List<User> getAllUserByTeamId(@PathParam("id") Long teamId) {
        return userService.getAllUserByTeam(teamId);
    }

    @GET
    @Path("{id}/tasks")
    public List<Task> getAllTasksByTeam(@PathParam("id") Long teamId) {
        return taskService.getAllTasksByTeamId(teamId);
    }

    @POST
    @AuthBinding
    public Response createTeam(Team team) {
        Team result = teamService.createTeam(team);

        return Response.status(CREATED).header("Location", "Team/" + result.getId()).build();
    }

    @PUT
    @Path("{id}/users/")
    public Response addUserToTeam(@PathParam("id") Long teamId, User user) {
        return teamService.addUserToTeam(teamId, user.getId())
                .map(u -> Response.status(OK))
                .orElse(Response.status((NOT_FOUND)))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long teamId, Team team) {
        return teamService.update(teamId, team)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("{id}/deactivate")
    public Response deActivate(@PathParam("id") Long teamId) {
        return teamService.deActivate(teamId)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }
}

