package com.personal.njtodo.endpoints.DTOs;

import java.util.Set;

public class TaskCreationDTO {
  private String taskName;
  private String description;

  private Set<String> assignees;

  public TaskCreationDTO(String taskName, Long projectId, String description, Set<String> assignees) {
    this.taskName = taskName;
    this.description = description;
    this.assignees = assignees;
  }

  public String getTaskName() {
    return this.taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<String> getAssignees() {
    return this.assignees;
  }

  public void setAssignees(Set<String> assignees) {
    this.assignees = assignees;
  }

}
