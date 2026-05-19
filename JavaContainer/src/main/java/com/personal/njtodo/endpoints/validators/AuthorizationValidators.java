package com.personal.njtodo.endpoints.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;

import com.personal.njtodo.endpoints.DTOs.UserClaimDTO;
import com.personal.njtodo.utilities.ResponseCompatibleException;

public class AuthorizationValidators {
  private static final boolean match(String regex, String claim) {
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(claim);
    return matcher.find();
  }

  public static final void validateLoginHeader(String headerString) throws ResponseCompatibleException {
    if (!match("^[\\w\\d.]+:[\\w\\d@#!]+$", headerString))
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Credentials")).logAndThrow();
  }

  public static final void validateEmailFormat(String email) throws ResponseCompatibleException {
    if (!match("^[\\w\\d.]+@[\\w.]+\\.[a-z]{2,4}$", email))
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Email Format")).logAndThrow();
  }

  public static final void validateUsernameFormat(String usrename) throws ResponseCompatibleException {
    final boolean format = match("^[\\w\\d.]+$", usrename);
    final boolean bounds = usrename.length() >= 2 && usrename.length() <= 32;
    if (!format || !bounds)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Username Format")).logAndThrow();
  }

  public static final boolean validatePremiumString(String premium) throws ResponseCompatibleException {
    if (premium.equals("premium"))
      return true;
    if (premium.equals("standard"))
      return false;
    (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Account Type")).logAndThrow();
    return false;
  }

  public static final void validatePasswordFormat(String password) throws ResponseCompatibleException {
    final boolean match = match("^[\\w\\d@#!]+$", password);
    final boolean bounds = password.length() >= 4 && password.length() <= 12;
    if(!match)(new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Password Format, Accepted: '^[\\w\\d@#!]+$'")).logAndThrow();
    if (!bounds)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "Invalid Password Length: minimum is 4, maximum is 12")).logAndThrow();
  }

  public static final boolean validatePayload(UserClaimDTO payload) throws ResponseCompatibleException {
    if (payload == null || payload.getUserInfo() == null)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "No information provided in the request body"))
          .logAndThrow();
    if (payload.getUserInfo().getEmail() == null || payload.getUserInfo().getUsername() == null
        || payload.getUserInfo().getType() == null || payload.getPassword() == null)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(),
          "Incomplete information provided in the request body")).logAndThrow();
    AuthorizationValidators.validateEmailFormat(payload.getUserInfo().getEmail());
    AuthorizationValidators.validateUsernameFormat(payload.getUserInfo().getUsername());
    final Boolean isPremium = AuthorizationValidators.validatePremiumString(payload.getUserInfo().getType());
    AuthorizationValidators.validatePasswordFormat(payload.getPassword());
    return isPremium;
  }
}
