package com.personal.njtodo.endpoints.DTOs.support_classes;

public class UserCoreData {
  private Long id;
  private String email;
  private String username;

  public UserCoreData(Long id, String email, String username) {
    this.email = email;
    this.id = id;
    this.username = username;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"username\":\"%s\", \"email\":\"%s\"}", getId(), getUsername(), getEmail());
  }

}
