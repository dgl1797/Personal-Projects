package com.personal.njtodo.endpoints.DTOs;

import java.util.Set;

import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.utilities.Converters;

public class TaskUpdateDTO {
  private String newDescription;
  private StateEnum newState;
  private Set<Long> newAssignees;

  public TaskUpdateDTO(String newDescription, String newState, Set<Long> newAssignees) {
    this.newDescription = newDescription;
    this.newState = Converters.stringToStateEnum(newState);
    this.newAssignees = newAssignees;
  }

  public String getNewDescription() {
    return this.newDescription;
  }

  public void setNewDescription(String newDescription) {
    this.newDescription = newDescription;
  }

  public StateEnum getNewState() {
    return this.newState;
  }

  public void setNewState(StateEnum newState) {
    this.newState = newState;
  }

  public Set<Long> getNewAssignees() {
    return this.newAssignees;
  }

  public void setNewAssignees(Set<Long> newAssignees) {
    this.newAssignees = newAssignees;
  }

}
