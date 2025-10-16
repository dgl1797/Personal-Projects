package com.personal.njtodo.EJBs.services.projections;

import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.utilities.Converters;

public class TaskWithProjectNameProjection {
  private Long id;
  private String name;
  private Long pjid;
  private String project;
  private StateEnum state;

  public TaskWithProjectNameProjection(Long id, String name, Long pjid, String project, StateEnum state) {
    this.id = id;
    this.name = name;
    this.pjid = pjid;
    this.project = project;
    this.state = state;
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

  public Long getPjid() {
    return this.pjid;
  }

  public void setPjid(Long pjid) {
    this.pjid = pjid;
  }

  public String getProject() {
    return this.project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getState() {
    return Converters.stateEnumToString(this.state);
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

}
