package com.personal.njtodo.EJBs.services;

import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.personal.njtodo.EJBs.entities.Project;
import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.entities.UserParticipateProject;
import com.personal.njtodo.EJBs.entities.support_objects.UserProjectId;
import com.personal.njtodo.EJBs.repositories.mysql.ProjectSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.TaskSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.UppSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.UserSqlRepo;
import com.personal.njtodo.EJBs.repositories.redis.UserRedisRepo;
import com.personal.njtodo.endpoints.DTOs.ProjectDTO;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ProjectService {
  private final ProjectSqlRepo projectSqlRepo;
  private final UserSqlRepo userSqlRepo;
  private final UppSqlRepo uppSqlRepo;
  private final UserRedisRepo userRedisRepo;
  private final TaskSqlRepo taskSqlRepo;
  private final EntityManager entityManager;

  public ProjectService(@Autowired ProjectSqlRepo projectSqlRepo, @Autowired UserSqlRepo userSqlRepo,
      @Autowired EntityManager entityManager, @Autowired UppSqlRepo uppSqlRepo, @Autowired UserRedisRepo userRedisRepo,
      @Autowired TaskSqlRepo taskSqlRepo) {
    this.projectSqlRepo = projectSqlRepo;
    this.userSqlRepo = userSqlRepo;
    this.uppSqlRepo = uppSqlRepo;
    this.userRedisRepo = userRedisRepo;
    this.taskSqlRepo = taskSqlRepo;
    this.entityManager = entityManager;
  }

  public Project getProjectByNameAndOwner(String owner, String name) {
    return projectSqlRepo.getByNameAndOwner(owner, name);
  }

  @Transactional
  public Long save(String pname, String owner) {
    User ownerUser = userSqlRepo.getUserByUsername(owner);
    ownerUser = entityManager.merge(ownerUser);
    return projectSqlRepo.save(new Project(pname, ownerUser)).getId();
  }

  @Transactional
  public Long save(String pname, String owner, Map<Long, String> participants) {
    User ownerUser = entityManager.merge(userSqlRepo.getUserByUsername(owner));
    Project newProject = entityManager.merge(projectSqlRepo.save(new Project(pname, ownerUser)));
    participants.entrySet().stream()
        .map(entry -> Map.entry(entityManager.getReference(User.class, entry.getKey()), entry.getValue()))
        .filter(entry -> entry.getKey() != null)
        .forEach(entry -> uppSqlRepo.save(new UserParticipateProject(entry.getKey(), newProject, entry.getValue())));
    return newProject.getId();
  }

  @Transactional
  public ProjectDTO getFullProjectById(Long pjid, String requestingUser) throws ResponseCompatibleException {
    try {
      Project takenProject = entityManager.merge(projectSqlRepo.findById(pjid).orElseThrow());
      return new ProjectDTO(takenProject, requestingUser);
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "Project doesn't exist")).logAndThrow();
      return null;
    }
  }

  public void checkAppartenence(String requestingUser, Long projectId) throws ResponseCompatibleException {
    try {
      Project requestedProject = projectSqlRepo.findById(projectId).orElseThrow();
      if (!requestedProject.getOwner().getUsername().equals(requestingUser)) {
        userRedisRepo.newIllegalAction(requestingUser, "unhautorized modification");
        (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(),
            "You are not allowed to add task in this project because you don't own it!")).logAndThrow();
      }
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "Requested project doesn't exist")).logAndThrow();
    }
  }

  @Transactional
  public void checkAccess(Long projectId, String requestingUser) throws ResponseCompatibleException {
    try {
      Project requestedProject = projectSqlRepo.findById(projectId).orElseThrow();
      if (requestedProject.getOwner().getUsername().equals(requestingUser))
        return;
      if (requestedProject.getParticipants().stream()
          .anyMatch(upp -> upp.getUser().getUsername().equals(requestingUser)))
        return;
      userRedisRepo.newIllegalAction(requestingUser, "illegal access");
      (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "Can't access this project")).logAndThrow();
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "Project doesn't exist")).logAndThrow();
    }
  }

  @Transactional
  public void delete(Long projectId) throws ResponseCompatibleException {
    try {
      Project requestedProject = projectSqlRepo.findById(projectId).orElseThrow();
      projectSqlRepo.delete(requestedProject);
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "Project doesn't exist")).logAndThrow();
    }
  }

  @Transactional
  public void removeUserFromProject(Long projectId, Long userId) throws ResponseCompatibleException {
    try {
      UserParticipateProject upp = entityManager
          .merge(uppSqlRepo.findById(new UserProjectId(userId, projectId)).orElseThrow());
      Project project = entityManager.getReference(Project.class, upp.getProject().getId());
      project.getTasks().stream().forEach(t -> {
        HashSet<User> finalSet = new HashSet<>();
        t.getAssignees().stream().forEach(u -> {
          if (u.getId() != userId)
            finalSet.add(u);
        });
        t.setAssignees(finalSet);
        taskSqlRepo.save(t);
      });
      uppSqlRepo.delete(upp);
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "User isn't assigned to that project")).logAndThrow();
    }

  }

  @Transactional
  public void appendParticipants(Long projectId, Map<Long, String> newParticipants) throws ResponseCompatibleException {
    try {
      Project requestedProject = entityManager.merge(projectSqlRepo.findById(projectId).orElseThrow());
      newParticipants.entrySet().stream().forEach(entry -> {
        User user = entityManager.merge(userSqlRepo.findById(entry.getKey()).orElseThrow());
        UserParticipateProject upp = new UserParticipateProject(user, requestedProject, entry.getValue());
        uppSqlRepo.save(upp);
      });
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "Some requested entity doesn't exist")).logAndThrow();
    }
  }

}
