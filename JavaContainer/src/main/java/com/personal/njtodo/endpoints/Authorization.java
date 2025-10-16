package com.personal.njtodo.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.services.UserService;
import com.personal.njtodo.endpoints.DTOs.UserClaimDTO;
import com.personal.njtodo.endpoints.validators.AuthorizationValidators;
import com.personal.njtodo.filters.name_bindings.AuthenticateToken;
import com.personal.njtodo.utilities.AccessManager;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/auth")
public class Authorization {

  private final UserService userService;

  public Authorization(@Autowired UserService userService) {
    this.userService = userService;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAuthFromUser(@NotNull @HeaderParam("authorization") String authRequest) {
    try {
      AuthorizationValidators.validateLoginHeader(authRequest);
      final String[] fields = authRequest.split(":");
      final String username = fields[0];
      final String passwordClaim = fields[1];
      final String token = userService.getUserAuthByUsername(username, passwordClaim);
      return Response.status(HttpStatus.OK.value()).entity(token).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postUserAuth(UserClaimDTO payloadUser) {
    try {
      final boolean isPremium = AuthorizationValidators.validatePayload(payloadUser);
      final User newUser = new User(payloadUser.getUserInfo().getEmail(), payloadUser.getPassword(),
          payloadUser.getUserInfo().getUsername(), isPremium);
      final String token = userService.save(newUser);
      return Response.status(200).entity(token).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @DELETE
  @Path("/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response deleteUserAuth(@NotNull @HeaderParam("authorization") String authRequest) {
    try {
      final String token = authRequest.split(" ")[1].strip();
      String callingUsername = AccessManager.parseToken(token).get("user", String.class);
      userService.signOut(callingUsername);
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }
}
