package com.personal.njtodo.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.personal.njtodo.EJBs.services.UserService;
import com.personal.njtodo.endpoints.DTOs.UserUpdateDTO;
import com.personal.njtodo.endpoints.DTOs.UserWithProjectsDTO;
import com.personal.njtodo.filters.name_bindings.AuthenticateToken;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/users")
public class UsersEndpoints {

  private final UserService userService;

  public UsersEndpoints(@Autowired UserService userService) {
    this.userService = userService;
  }

  @GET
  @Path("/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response getUserDashboard(@PathParam("username") String username) {
    try {
      UserWithProjectsDTO user = userService.getFullUserByUsername(username);
      return Response.status(200).entity(user).build();
    } catch (ResponseCompatibleException e) {
      return Response.status(e.getStatus()).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/{username}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response updateUserProfile(@PathParam("username") String username, UserUpdateDTO payload,
      @HeaderParam("username") String requestingUser) {
    try {
      if (!requestingUser.equals(username))
        return Response.status(401).entity("You can only update your own profile").build();
      userService.updateUser(username, payload);
      return Response.status(200).entity("Profile updated successfully").build();
    } catch (ResponseCompatibleException e) {
      return Response.status(e.getStatus()).entity(e.getMessage()).build();
    }
  }

}
