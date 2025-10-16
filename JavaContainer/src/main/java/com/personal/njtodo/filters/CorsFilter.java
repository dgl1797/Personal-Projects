package com.personal.njtodo.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Component
@Provider
public class CorsFilter implements ContainerResponseFilter {

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
      throws IOException {

    final List<String> allowrdOrigins = List.of(System.getenv("ALLOWED_ORIGINS").split(","));
    final String origin = requestContext.getHeaderString("Origin");
    if (!allowrdOrigins.contains(origin)) {
      requestContext.abortWith(Response.status(HttpStatus.UNAUTHORIZED.value())
          .entity(String.format("unallowed origin: %s", origin)).build());
      return;
    }

    // origin -> identifies the origins that can access the content provided by this origin
    responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);

    // credentials -> 
    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

    // headers
    responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");

    // methods
    responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
  }

}
