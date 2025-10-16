package com.personal.njtodo.EJBs.entities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;
import com.personal.njtodo.utilities.Converters;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "task", uniqueConstraints = @UniqueConstraint(columnNames = { "pjid", "name" }))
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotEmpty
  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "pjid", referencedColumnName = "id", nullable = false)
  private Project project;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  private StateEnum state;

  @Column(name = "description")
  private String description;

  @ManyToMany(cascade = CascadeType.ALL)
  //@formatter:off
  @JoinTable(
    name = "user_execute_task", 
    joinColumns = @JoinColumn(name = "tid", referencedColumnName = "id", nullable = false),
    inverseJoinColumns = @JoinColumn(name = "uid", referencedColumnName = "id", nullable = false)
  )
  //@formatter:on
  private Set<User> assignees;

  public Task() {}

  public Task(Project belongsTo, String name, StateEnum state) {
    this.project = belongsTo;
    this.name = name;
    this.state = state;
    this.description = null;
    this.assignees = new HashSet<>();
  }

  public Task(Project belongsTo, String name, StateEnum state, Set<User> assignees) {
    this.project = belongsTo;
    this.name = name;
    this.state = state;
    this.description = null;
    this.assignees = assignees;
  }

  public Task(Project belongsTo, String name, StateEnum state, String description) {
    this.project = belongsTo;
    this.name = name;
    this.state = state;
    this.description = description;
    this.assignees = new HashSet<>();
  }

  public Task(Project belongsTo, String name, StateEnum state, String description, Set<User> assignees) {
    this.project = belongsTo;
    this.name = name;
    this.state = state;
    this.description = description;
    this.assignees = assignees;
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

  public Project getProject() {
    return this.project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public StateEnum getState() {
    return this.state;
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<User> getAssignees() {
    return this.assignees;
  }

  public void setAssignees(Set<User> assignees) {
    this.assignees = assignees;
  }

  @Override
  public String toString() {
    return String.format(
        "{\"id\":\"%d\", \"name\":\"%s\", \"state\":\"%s\", \"description\":\"%s\", \"project\":\"%s\", \"assignees\":\"%s\"}",
        getId(), getName(), Converters.stateEnumToString(getState()), getDescription(), getProject(),
        Arrays.toString(getAssignees().stream().map(assign -> assign.getUsername()).toArray()));
  }

}
