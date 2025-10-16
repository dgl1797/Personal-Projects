package com.personal.njtodo.endpoints.DTOs.support_classes;

import java.util.Arrays;
import java.util.Set;

public class ProjectInfo {
  private Long id;

  private String name;

  private Set<ParticipantCoreData> participants;

  private Set<TaskCoreData> tasks;

  public ProjectInfo(Long id, String name, UserCoreData userData, ParticipantCoreData[] participants,
      TaskCoreData[] tasks) {
    this.id = id;
    this.name = name;
    this.participants = Set.of(participants);
    this.tasks = Set.of(tasks);
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

  public Set<ParticipantCoreData> getParticipants() {
    return this.participants;
  }

  public void setParticipants(Set<ParticipantCoreData> participants) {
    this.participants = participants;
  }

  public Set<TaskCoreData> getTasks() {
    return this.tasks;
  }

  public void setTasks(Set<TaskCoreData> tasks) {
    this.tasks = tasks;
  }

  @Override
  public String toString() {
    return String.format("{\"id\":\"%d\", \"name\":\"%s\", \"participants\":\"%s\", \"tasks\":\"%s\"}", getId(),
        getName(), Arrays.toString(getParticipants().stream().map(p -> p.toString()).toArray()),
        Arrays.toString(getTasks().stream().map(t -> t.toString()).toArray()));
  }

}
