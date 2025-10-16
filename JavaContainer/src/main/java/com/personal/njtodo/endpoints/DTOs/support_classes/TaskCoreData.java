package com.personal.njtodo.endpoints.DTOs.support_classes;

import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.utilities.Converters;

public class TaskCoreData {
  private Long id;
  private String state;
  private String name;
  private String description;

  public TaskCoreData(Long id, String name, StateEnum state, String description) {
    this.id = id;
    this.state = Converters.stateEnumToString(state);
    this.name = name;
    this.description = description;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getState() {
    return this.state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"name\":\"%s\", \"state\":\"%s\", \"description\":\"%s\"}", getId(),
        getName(), getState(), getDescription());
  }

}
