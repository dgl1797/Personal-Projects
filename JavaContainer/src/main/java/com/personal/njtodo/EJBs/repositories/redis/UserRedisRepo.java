package com.personal.njtodo.EJBs.repositories.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.personal.njtodo.utilities.ResponseCompatibleException;

@Service
public class UserRedisRepo {
  private final RedisTemplate<String, Object> redisTemplate;

  public UserRedisRepo(@Autowired RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void saveSessionForUser(String username, String token) {
    redisTemplate.opsForHash().put("sessions", username, token);
  }

  public String getSessionFromUser(String username) throws ResponseCompatibleException {
    final String sessionToken = (String) redisTemplate.opsForHash().get("sessions", username);
    if (sessionToken == null)
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(),
          String.format("No session found for user %s", username))).logAndThrow();
    return sessionToken;
  }

  public void newIllegalAction(String username, String action) {
    redisTemplate.opsForHash().put("illegal-action", username, action);
  }

  public void deleteSession(String username) {
    redisTemplate.opsForHash().delete("sessions", username);
  }

  public String tryGetSessionForUser(String username) {
    return (String) redisTemplate.opsForHash().get("sessions", username);
  }
}