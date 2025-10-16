package com.personal.njtodo.endpoints.DTOs.support_classes;

import java.sql.Timestamp;

public class ParticipantCoreData {
  private Long id;
  private String username;
  private String role;
  private String email;
  private String addedTime;

  public ParticipantCoreData(Long id, String username, String email, String role, Timestamp addedTime) {
    this.id = id;
    this.username = username;
    this.role = role;
    this.email = email;
    this.addedTime = addedTime.toString();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddedTime() {
    return this.addedTime;
  }

  public void setAddedTime(String addedTime) {
    this.addedTime = addedTime;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"username\":\"%s\", \"email\":\"%s\", \"role\":\"%s\", \"added\":\"%s\"}",
        getId(), getUsername(), getEmail(), getRole(), getAddedTime());
  }

}
