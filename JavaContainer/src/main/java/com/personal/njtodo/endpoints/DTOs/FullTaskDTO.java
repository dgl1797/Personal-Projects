package com.personal.njtodo.endpoints.DTOs;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.utilities.Converters;

public class FullTaskDTO {
  private Long id;
  private String name;
  private String state;
  private String description;

  private Map<Long, String> assignees;

  public FullTaskDTO(Long id, String name, StateEnum state, String description, Set<User> assignees) {
    this.id = id;
    this.name = name;
    this.state = Converters.stateEnumToString(state);
    this.description = description;
    this.assignees = assignees.stream().map(user -> Map.entry(user.getId(), user.getUsername()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getState() {
    return this.state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<Long, String> getAssignees() {
    return this.assignees;
  }

  public void setAssignees(Map<Long, String> assignees) {
    this.assignees = assignees;
  }

}
