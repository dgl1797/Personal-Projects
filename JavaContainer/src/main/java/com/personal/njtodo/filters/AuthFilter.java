package com.personal.njtodo.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.personal.njtodo.EJBs.services.UserService;
import com.personal.njtodo.endpoints.DTOs.UserDTO;
import com.personal.njtodo.filters.name_bindings.AuthenticateToken;
import com.personal.njtodo.utilities.AccessManager;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Component
@Provider
@Priority(Priorities.AUTHORIZATION)
@AuthenticateToken
public class AuthFilter implements ContainerRequestFilter {

  private final UserService userService;

  public AuthFilter(@Autowired UserService userService) {
    this.userService = userService;
  }

  /**
   * abort will automatically cancel the request, if it doesn't happen the JAX-RS method will be called
   * normally
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    try {
      // validate authorization header
      String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        requestContext
            .abortWith(Response.status(HttpStatus.BAD_REQUEST.value()).entity("Bad Authorization Format").build());
        return;
      }

      // validate the token
      String[] parts = authHeader.split(" ");
      String tokenClaim = parts.length > 1 ? authHeader.split(" ")[1].strip() : null;
      if (tokenClaim == null) {
        requestContext.abortWith(Response.status(HttpStatus.BAD_REQUEST.value()).entity("Token not provided").build());
        return;
      }

      // validate coherence of request and user
      Claims claims = AccessManager.parseToken(tokenClaim);
      UserDTO claimedInfo = new UserDTO(claims.get("user", String.class), claims.get("email", String.class),
          claims.get("type", String.class));
      String requestingUser = requestContext.getUriInfo().getPathParameters().getFirst("username");
      if (requestingUser != null && !requestingUser.equals(claimedInfo.getUsername())) {
        userService.storeIllegalAccess(claimedInfo.getUsername());
        requestContext.abortWith(Response.status(HttpStatus.UNAUTHORIZED.value())
            .entity("Not Authorized to access this user's info").build());
        return;
      }
      requestingUser = claimedInfo.getUsername();

      // check user has not logged out
      if (userService.getStoredSessionForUser(requestingUser) == null) {
        requestContext.abortWith(Response.status(HttpStatus.UNAUTHORIZED.value()).entity("Not logged in").build());
        return;
      }

      // validate claimed info
      userService.validateClaims(claimedInfo, claims.getExpiration());
      requestContext.getHeaders().add("username", requestingUser);
      requestContext.getHeaders().add("user-type", claimedInfo.getType());

    } catch (ResponseCompatibleException rce) {
      requestContext.abortWith(Response.status(rce.getStatus()).entity(rce.getMessage()).build());
    }

  }

}
