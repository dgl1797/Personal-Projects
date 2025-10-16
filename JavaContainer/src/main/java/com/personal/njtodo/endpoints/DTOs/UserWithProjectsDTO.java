package com.personal.njtodo.endpoints.DTOs;

import java.util.Map;
import java.util.Set;

import com.personal.njtodo.EJBs.services.projections.ParticipationProjection;
import com.personal.njtodo.EJBs.services.projections.TaskWithProjectNameProjection;

public class UserWithProjectsDTO {
  private final UserDTO userInfo;
  private final Map<Long, String> ownedProjects;
  private final Set<ParticipationProjection> participationSet;
  private final Set<TaskWithProjectNameProjection> uncompleteTasks;

  public UserWithProjectsDTO(UserDTO userinfo, Map<Long, String> ownedProjects,
      Set<ParticipationProjection> participations, Set<TaskWithProjectNameProjection> uncompleteTasks) {
    this.userInfo = userinfo;
    this.ownedProjects = ownedProjects;
    this.participationSet = participations;
    this.uncompleteTasks = uncompleteTasks;
  }

  public UserDTO getUserInfo() {
    return this.userInfo;
  }

  public Map<Long, String> getOwnedProjects() {
    return this.ownedProjects;
  }

  public Set<ParticipationProjection> getParticipationSet() {
    return this.participationSet;
  }

  public Set<TaskWithProjectNameProjection> getUncompleteTasks() {
    return this.uncompleteTasks;
  }

}
