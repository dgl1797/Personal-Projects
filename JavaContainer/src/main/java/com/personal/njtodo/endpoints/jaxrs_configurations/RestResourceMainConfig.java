package com.personal.njtodo.endpoints.jaxrs_configurations;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.personal.njtodo.endpoints.Authorization;
import com.personal.njtodo.endpoints.ProjectEndpoints;
import com.personal.njtodo.endpoints.UsersEndpoints;
import com.personal.njtodo.filters.AuthFilter;
import com.personal.njtodo.filters.CorsFilter;

@Component
public class RestResourceMainConfig extends ResourceConfig {
  public RestResourceMainConfig() {
    /** ENDPOINTS */
    register(Authorization.class);
    register(UsersEndpoints.class);
    register(ProjectEndpoints.class);

    /** FILTERS */
    register(AuthFilter.class);
    register(CorsFilter.class);
  }
}
