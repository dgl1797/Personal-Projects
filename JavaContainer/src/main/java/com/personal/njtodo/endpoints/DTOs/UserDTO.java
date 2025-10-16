package com.personal.njtodo.endpoints.DTOs;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.services.projections.UserAuthProjection;

public class UserDTO {
  private String username;
  private String email;
  private String type;

  public UserDTO(String username, String email, String type) {
    this.username = username;
    this.email = email;
    this.type = type;
  }

  public static UserDTO from(UserAuthProjection userauth) {
    return new UserDTO(userauth.getUsername(), userauth.getEmail(), (userauth.getPremium() ? "premium" : "standard"));
  }

  public static UserDTO from(User user) {
    return new UserDTO(user.getUsername(), user.getEmail(), (user.isPremium() ? "premium" : "standard"));
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return String.format("{\"username\":\"%s\", \"email\":\"%s\", \"type\":\"%s\"}", getUsername(), getEmail(),
        getType());
  }

}
