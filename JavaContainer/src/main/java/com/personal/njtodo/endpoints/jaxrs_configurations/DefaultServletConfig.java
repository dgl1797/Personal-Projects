package com.personal.njtodo.endpoints.jaxrs_configurations;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.servlet.Servlet;

@Configuration
@Primary
public class DefaultServletConfig {

  @Bean
  public ServletRegistrationBean<Servlet> defaultJerseyRegistrationBean(RestResourceMainConfig serviceConfig) {
    ServletRegistrationBean<Servlet> defaultJersey = new ServletRegistrationBean<>(new ServletContainer(serviceConfig));
    defaultJersey.addUrlMappings("/apis/*");
    defaultJersey.setName("DefaultJersey");
    defaultJersey.setLoadOnStartup(0);

    return defaultJersey;
  }
}
