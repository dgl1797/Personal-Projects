package com.personal.njtodo.utilities;

public class AuthInformation {
  private String generatedPassword;
  private String generatedSalt;

  public AuthInformation(String pass, String salt) {
    this.generatedPassword = pass;
    this.generatedSalt = salt;
  }

  public String getGeneratedPassword() {
    return this.generatedPassword;
  }

  public String getGeneratedSalt() {
    return this.generatedSalt;
  }

}
