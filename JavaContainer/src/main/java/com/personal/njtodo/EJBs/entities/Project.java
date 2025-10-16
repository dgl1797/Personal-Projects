package com.personal.njtodo.EJBs.entities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "project", uniqueConstraints = @UniqueConstraint(columnNames = { "owner", "name" }))
public class Project {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "owner", referencedColumnName = "username", nullable = false)
  private User owner;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  private Set<UserParticipateProject> participants;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Task> tasks;

  public Project() {}

  public Project(String name, User owner) {
    this.name = name;
    this.owner = owner;
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

  public User getOwner() {
    return this.owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Set<UserParticipateProject> getParticipants() {
    return this.participants == null ? new HashSet<>() : this.participants;
  }

  public void setParticipants(Set<UserParticipateProject> participants) {
    this.participants = participants;
  }

  public Set<Task> getTasks() {
    return this.tasks == null ? new HashSet<>() : this.tasks;
  }

  public void setTasks(Set<Task> tasks) {
    this.tasks = tasks;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"name\":\"%s\", \"owner\":\"%s\"}", getId(), getName(), getOwner(),
        Arrays.toString(getParticipants().stream().map(p -> String.format("%s", p.getUser().getUsername())).toArray()),
        Arrays.toString(getTasks().stream().map(t -> String.format("%s", t.getName())).toArray()));
  }

}
