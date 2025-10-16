package com.personal.njtodo.endpoints.DTOs;

public class UserClaimDTO {
  private UserDTO userInfo;
  private String password;

  public UserClaimDTO(UserDTO userInfo, String passwordClaim) {
    this.userInfo = userInfo;
    this.password = passwordClaim;
  }

  public UserDTO getUserInfo() {
    return this.userInfo;
  }

  public void setUserInfo(UserDTO userInfo) {
    this.userInfo = userInfo;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String passwordClaim) {
    this.password = passwordClaim;
  }

  @Override
  public String toString() {
    return String.format("\"userInfo\":\"%s\", \"passwordClaim\":\"%s\"", getUserInfo(), getPassword());
  }

}
