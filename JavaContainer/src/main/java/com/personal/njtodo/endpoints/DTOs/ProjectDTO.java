package com.personal.njtodo.endpoints.DTOs;

import com.personal.njtodo.EJBs.entities.Project;
import com.personal.njtodo.endpoints.DTOs.support_classes.ParticipantCoreData;
import com.personal.njtodo.endpoints.DTOs.support_classes.ProjectInfo;
import com.personal.njtodo.endpoints.DTOs.support_classes.TaskCoreData;
import com.personal.njtodo.endpoints.DTOs.support_classes.UserCoreData;

public class ProjectDTO {
  private ProjectInfo projectInfo;
  private String owner;

  public ProjectDTO(Project p, String requester) {
    this.owner = p.getOwner().getUsername();
    this.projectInfo = new ProjectInfo(p.getId(), p.getName(),
        new UserCoreData(p.getOwner().getId(), p.getOwner().getEmail(), p.getOwner().getUsername()),
        p.getParticipants().stream()
            .map(upp -> new ParticipantCoreData(upp.getUser().getId(), upp.getUser().getUsername(),
                upp.getUser().getEmail(), upp.getRole(), upp.getAdded()))
            .toArray(ParticipantCoreData[]::new),
        p.getTasks().stream().map(t -> new TaskCoreData(t.getId(), t.getName(), t.getState(), t.getDescription()))
            .toArray(TaskCoreData[]::new));
  }

  public ProjectInfo getProject() {
    return this.projectInfo;
  }

  public void setProject(ProjectInfo project) {
    this.projectInfo = project;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setOwner(String newOwner) {
    this.owner = newOwner;
  }

  @Override
  public String toString() {
    return String.format("{\"project\":\"%s\", \"ownerRequested\":\"%s\"}", getProject(), getOwner());
  }
}
