package com.personal.njtodo.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.random.RandomGenerator;

import org.springframework.http.HttpStatus;

import com.personal.njtodo.endpoints.DTOs.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.security.auth.message.AuthException;

public class AccessManager {

  private static final RandomGenerator GENERATOR = new SecureRandom();
  private static final String JWT_SECRET = System.getenv("JWT_SECRET");

  public static final AuthInformation encrypt(String password) throws ResponseCompatibleException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] salt = new byte[16];
      GENERATOR.nextBytes(salt);
      String hexSalt = Converters.binaryToHex(salt);
      String plainText = String.format("%s%s", password, hexSalt);
      md.update(plainText.getBytes());
      String encription = Converters.binaryToHex(md.digest());
      return new AuthInformation(encription, hexSalt);
    } catch (NoSuchAlgorithmException nsae) {
      (new ResponseCompatibleException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Invalid Encription Type", nsae))
          .logAndThrow();
      return null;
    }
  }

  public static final void testPassword(String claimedPassword, String password, String salt)
      throws ResponseCompatibleException {
    try {
      String plainText = String.format("%s%s", claimedPassword, salt);
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(plainText.getBytes());
      String claim = Converters.binaryToHex(md.digest());
      if (!claim.equals(password))
        throw new AuthException("Invalid password");
    } catch (NoSuchAlgorithmException nsae) {
      (new ResponseCompatibleException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Encoding Error", nsae))
          .logAndThrow();
    } catch (AuthException ae) {
      (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "Invalid password", ae)).logAndThrow();
    }
  }

  public static final String generateToken(UserDTO userInfo)
      throws ResponseCompatibleException {
    try {
      return Jwts.builder().claims()
          .issuer("njtodo-app")
          .add("user", userInfo.getUsername())
          .add("email", userInfo.getEmail())
          .add("type", userInfo.getType())
          .expiration(Date.from(Instant.now().plus(Duration.ofDays(1L))))
          .and().signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes("UTF-8"))).compact();
    } catch (Exception e) {
      (new ResponseCompatibleException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Encription Error", e))
          .logAndThrow();
      return null;
    }
  }

  public static final Claims parseToken(final String token) throws ResponseCompatibleException {
    try {
      return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes("UTF-8")))
          .requireIssuer("njtodo-app")
          .build()
          .parseSignedClaims(token).getPayload();
    } catch (SignatureException se) {
      (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), se.getMessage(), se)).logAndThrow();
      return null;
    } catch (Exception e) {
      (new ResponseCompatibleException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Encription Error", e))
          .logAndThrow();
      return null;
    }
  }
}
