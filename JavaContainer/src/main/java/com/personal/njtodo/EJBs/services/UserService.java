package com.personal.njtodo.EJBs.services;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.EJBs.repositories.mysql.UserSqlRepo;
import com.personal.njtodo.EJBs.repositories.redis.UserRedisRepo;
import com.personal.njtodo.EJBs.services.projections.ParticipationProjection;
import com.personal.njtodo.EJBs.services.projections.TaskWithProjectNameProjection;
import com.personal.njtodo.EJBs.services.projections.UserAuthProjection;
import com.personal.njtodo.endpoints.DTOs.UserDTO;
import com.personal.njtodo.endpoints.DTOs.UserUpdateDTO;
import com.personal.njtodo.endpoints.DTOs.UserWithProjectsDTO;
import com.personal.njtodo.utilities.AccessManager;
import com.personal.njtodo.utilities.AuthInformation;
import com.personal.njtodo.utilities.Converters;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.transaction.Transactional;

@Service
public class UserService {
  private final UserSqlRepo userSqlRepo;
  private final UserRedisRepo userRedisRepo;

  // constructor instance injection
  public UserService(@Autowired UserSqlRepo userSqlRepo, @Autowired UserRedisRepo userRedisRepo) {
    this.userSqlRepo = userSqlRepo;
    this.userRedisRepo = userRedisRepo;
  }

  public String getUserAuthByUsername(String username, String passwordClaim) throws ResponseCompatibleException {
    UserAuthProjection userInfo = userSqlRepo.getUserAuthByUsername(username);
    if (userInfo == null)
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "Username doesn't exist")).logAndThrow();
    AccessManager.testPassword(passwordClaim, userInfo.getPassword(), userInfo.getSalt());
    String token = userRedisRepo.tryGetSessionForUser(username);
    if (token != null) {
      try {
        AccessManager.parseToken(token);
        return token;
      } catch (ResponseCompatibleException e) {
        System.out.println("token expired, setting new token for auth request");
      }
    }
    token = AccessManager.generateToken(UserDTO.from(userInfo));
    userRedisRepo.saveSessionForUser(username, token);
    return token;
  }

  public String save(User u) throws ResponseCompatibleException {
    final String token = AccessManager.generateToken(UserDTO.from(u));
    userSqlRepo.save(u);
    userRedisRepo.saveSessionForUser(u.getUsername(), token);
    return token;
  }

  public void storeIllegalAccess(String performedFrom) {
    userRedisRepo.newIllegalAction(performedFrom, "anauthorized access");
  }

  public void validateClaims(UserDTO userInfo, Date expiration) throws ResponseCompatibleException {
    UserAuthProjection userRealInfo = userSqlRepo.getUserAuthByUsername(userInfo.getUsername());
    Boolean claimsTobePremium = userInfo.getType().equals("premium");
    if (userRealInfo == null)
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "The specified user doesn't exist")).logAndThrow();
    if (!userRealInfo.getEmail().equals(userInfo.getEmail()) || !claimsTobePremium.equals(userRealInfo.getPremium())) {
      userRedisRepo.newIllegalAction(userRealInfo.getUsername(), "token manipulation");
      (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "Incompatible Information")).logAndThrow();
    }
    if (expiration.before(Date.from(Instant.now()))) {
      userRedisRepo.deleteSession(userInfo.getUsername());
      (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(),
          "Access token expired, please login again to confirm your identity.")).logAndThrow();
    }
  }

  public void signOut(String callingUsername) {
    userRedisRepo.deleteSession(callingUsername);
  }

  public String getStoredSessionForUser(String requestingUser) throws ResponseCompatibleException {
    return userRedisRepo.tryGetSessionForUser(requestingUser);
  }

  @Transactional
  public UserWithProjectsDTO getFullUserByUsername(String username) throws ResponseCompatibleException {
    User result = userSqlRepo.getUserByUsername(username);
    if (result == null)
      (new ResponseCompatibleException(404, String.format("User %s doesn't exist in database", username)))
          .logAndThrow();

    UserDTO userInfo = new UserDTO(result.getUsername(), result.getEmail(),
        Converters.booleanTypeToString(result.isPremium()));

    Map<Long, String> ownedProjects = result.getOwnedProjects().stream().map(pj -> Map.entry(pj.getId(), pj.getName()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    Set<ParticipationProjection> participationSet = Set.of(result.getParticipations().stream()
        .map(upp -> new ParticipationProjection(upp.getProject().getId(), upp.getProject().getName(), upp.getRole()))
        .toArray(ParticipationProjection[]::new));

    Set<TaskWithProjectNameProjection> uncompleteTasks = Set.of(result.getAssignedTasks().stream()
        .filter(t -> t.getState() != StateEnum.done).map(t -> new TaskWithProjectNameProjection(t.getId(), t.getName(),
            t.getProject().getId(), t.getProject().getName(), t.getState()))
        .toArray(TaskWithProjectNameProjection[]::new));

    return new UserWithProjectsDTO(userInfo, ownedProjects, participationSet, uncompleteTasks);
  }

  @Transactional
  public void updateUser(String username, UserUpdateDTO payload) throws ResponseCompatibleException {
    User user = userSqlRepo.getUserByUsername(username);
    if (user == null)
      (new ResponseCompatibleException(404, String.format("User %s doesn't exist", username))).logAndThrow();

    // Update email if provided
    if (payload.getEmail() != null && !payload.getEmail().isEmpty()) {
      user.setEmail(payload.getEmail());
    }

    // Update password if both current and new are provided
    if (payload.getCurrentPassword() != null && !payload.getCurrentPassword().isEmpty()
        && payload.getNewPassword() != null && !payload.getNewPassword().isEmpty()) {
      AccessManager.testPassword(payload.getCurrentPassword(), user.getPassword(), user.getSalt());
      AuthInformation auth = AccessManager.encrypt(payload.getNewPassword());
      user.setPassword(auth.getGeneratedPassword());
      user.setSalt(auth.getGeneratedSalt());
    }

    userSqlRepo.save(user);
  }

}
