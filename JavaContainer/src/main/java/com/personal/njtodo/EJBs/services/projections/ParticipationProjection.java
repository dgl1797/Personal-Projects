package com.personal.njtodo.EJBs.services.projections;

public class ParticipationProjection {
  private Long id;
  private String name;
  private String role;

  public ParticipationProjection(Long id, String name, String role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"name\":\"%s\", \"role\":\"%s\"}", getId(), getName(), getRole());
  }

}
