package com.personal.njtodo.EJBs.services;

import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.personal.njtodo.EJBs.entities.Project;
import com.personal.njtodo.EJBs.entities.Task;
import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.EJBs.repositories.mysql.ProjectSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.TaskSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.UserSqlRepo;
import com.personal.njtodo.EJBs.repositories.redis.UserRedisRepo;
import com.personal.njtodo.endpoints.DTOs.FullTaskDTO;
import com.personal.njtodo.endpoints.DTOs.TaskCreationDTO;
import com.personal.njtodo.endpoints.DTOs.TaskUpdateDTO;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class TaskService {

  private final TaskSqlRepo taskSqlRepo;
  private final ProjectSqlRepo projectSqlRepo;
  private final UserSqlRepo userSqlRepo;
  private final UserRedisRepo userRedisRepo;
  private final EntityManager entityManager;

  public TaskService(@Autowired TaskSqlRepo taskSqlRepo, @Autowired ProjectSqlRepo projectSqlRepo,
      @Autowired UserSqlRepo userSqlRepo, @Autowired EntityManager entityManager,
      @Autowired UserRedisRepo userRedisRepo) {
    this.taskSqlRepo = taskSqlRepo;
    this.projectSqlRepo = projectSqlRepo;
    this.userSqlRepo = userSqlRepo;
    this.entityManager = entityManager;
    this.userRedisRepo = userRedisRepo;
  }

  public Task getByNameAndProject(String taskName, Long projectID) {
    return taskSqlRepo.getByNameAndProject(taskName, projectID);
  }

  @Transactional
  public Long save(TaskCreationDTO payload, Long projectId) throws ResponseCompatibleException {
    try {
      Project belongsTo = entityManager.merge(projectSqlRepo.findById(projectId).orElseThrow());
      Set<User> assignees = Set.of(payload.getAssignees().stream()
          .filter(assignee -> assignee.equals(belongsTo.getOwner().getUsername()) || Set
              .of(belongsTo.getParticipants().stream().map(upp -> upp.getUser().getUsername()).toArray(String[]::new))
              .contains(assignee))
          .map(assignee -> {
            Long userId = userSqlRepo.getUserByUsername(assignee).getId();
            return entityManager.getReference(User.class, userId);
          }).filter(u -> u != null).toArray(User[]::new));
      String description = payload.getDescription() == null || payload.getDescription().equals("") ? null
          : payload.getDescription();
      Task task = taskSqlRepo.save(new Task(belongsTo, payload.getTaskName(), StateEnum.todo, description, assignees));
      return task.getId();
    } catch (IllegalArgumentException | NoSuchElementException e) {
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "invalid project selection")).logAndThrow();
      return null;
    }
  }

  @Transactional
  public void delete(Long projectId, Long taskId) throws ResponseCompatibleException {
    try {
      projectSqlRepo.findById(projectId).orElseThrow();
      Task requestedTask = taskSqlRepo.findById(taskId).orElseThrow();
      if (!requestedTask.getProject().getId().equals(projectId)) {
        userRedisRepo.newIllegalAction(requestedTask.getProject().getOwner().getUsername(), "illegal update");
        (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "Task doesn't belong to the project"))
            .logAndThrow();
      }
      taskSqlRepo.delete(requestedTask);
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "Project task doesn't exist")).logAndThrow();
    }
  }

  @Transactional
  public void updateService(Long taskId, Long projectId, String requestingUser, TaskUpdateDTO payload)
      throws ResponseCompatibleException {
    try {
      projectSqlRepo.findById(projectId).orElseThrow();
      Task requestedTask = taskSqlRepo.findById(taskId).orElseThrow();
      if (!requestedTask.getProject().getId().equals(projectId)) {
        userRedisRepo.newIllegalAction(requestedTask.getProject().getOwner().getUsername(), "illegal update");
        (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "Task doesn't belong to the project"))
            .logAndThrow();
      }
      if (payload.getNewDescription() != null && payload.getNewDescription() != "")
        requestedTask.setDescription(payload.getNewDescription());
      if (payload.getNewState() != null)
        requestedTask.setState(payload.getNewState());
      if (payload.getNewAssignees() != null && !payload.getNewAssignees().isEmpty()) {
        Set<User> newSet = requestedTask.getAssignees();
        newSet.addAll(payload.getNewAssignees().stream().map(uid -> userSqlRepo.findById(uid).orElseThrow()).toList());
        requestedTask.setAssignees(newSet);
      }
      taskSqlRepo.save(requestedTask);
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(HttpStatus.NOT_FOUND.value(), "Project task doesn't exist")).logAndThrow();
    }
  }

  public void checkAppartenence(Long taskId, Long projectId) throws ResponseCompatibleException {
    try {
      if (!taskSqlRepo.findById(taskId).orElseThrow().getProject().getId().equals(projectId))
        (new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(), "task doesn't belong to project"))
            .logAndThrow();
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "task doesn't exist")).logAndThrow();
    }
  }

  @Transactional
  public FullTaskDTO getFullById(Long taskId) throws ResponseCompatibleException {
    try {
      Task requestedTask = taskSqlRepo.findById(taskId).orElseThrow();
      return new FullTaskDTO(requestedTask.getId(), requestedTask.getName(), requestedTask.getState(),
          requestedTask.getDescription(), requestedTask.getAssignees());
    } catch (NoSuchElementException e) {
      (new ResponseCompatibleException(404, "task doesn't exist")).logAndThrow();
      return null;
    }
  }
}
