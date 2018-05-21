package se.backend.groupred2.resource;

import org.springframework.stereotype.Component;
import se.backend.groupred2.model.Team;
import se.backend.groupred2.model.User;
import se.backend.groupred2.resource.filter.AuthBinding;
import se.backend.groupred2.service.TeamService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("teams")
public final class TeamResource {
    private final TeamService service;

    public TeamResource(TeamService service) {
        this.service = service;
    }

    @GET
    @Path("{id}")
    public Response getTeam(@PathParam("id") Long id) { return Response.ok(service.getTeam(id)).build(); }

    @GET
    public Response getAllTeams() {
        return Response.ok(service.getAllTeams()).build();
    }

    @POST
    @AuthBinding
    public Response createTeam(Team team) {
        Team result = service.createTeam(team);

        return Response.status(CREATED).header("Location", "Team/" + result.getId()).build();
    }

    @PUT
    @Path("{id}/users/")
    public Response addUser(@PathParam("id") Long teamId, User user) {
        return service.addUser(teamId, user.getId())
                .map(u -> Response.status(OK))
                .orElse(Response.status((NOT_FOUND)))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long teamId, Team team) {
        return service.update(teamId, team)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @PUT
    @Path("{id}/deactivate")
    public Response deActivate(@PathParam("id") Long teamId) {
        return service.deActivate(teamId)
                .map(t -> Response.status(OK))
                .orElse(Response.status(NOT_FOUND))
                .build();
    }
}

