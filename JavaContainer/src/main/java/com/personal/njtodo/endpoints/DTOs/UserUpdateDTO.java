package com.personal.njtodo.endpoints.DTOs;

public class UserUpdateDTO {
  private String email;
  private String currentPassword;
  private String newPassword;

  public UserUpdateDTO() {}

  public UserUpdateDTO(String email, String currentPassword, String newPassword) {
    this.email = email;
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCurrentPassword() {
    return this.currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return this.newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}