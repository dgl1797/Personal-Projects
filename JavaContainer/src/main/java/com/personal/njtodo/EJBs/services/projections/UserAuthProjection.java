package com.personal.njtodo.EJBs.services.projections;

public interface UserAuthProjection {
  public Long getId();

  public String getUsername();

  public String getEmail();

  public String getSalt();

  public String getPassword();

  public Boolean getPremium();
}
