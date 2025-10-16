package com.personal.njtodo.endpoints.DTOs;

import java.util.Arrays;
import java.util.Map;

public class ProjectCreationDTO {
  private String projectName;
  private Map<Long, String> participants;

  public ProjectCreationDTO(String pname, Map<Long, String> pids) {
    this.projectName = pname;
    this.participants = pids;
  }

  public String getProjectName() {
    return this.projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public Map<Long, String> getParticipants() {
    return this.participants;
  }

  public void setParticipantIds(Map<Long, String> participants) {
    this.participants = participants;
  }

  @Override
  public String toString() {
    return String.format("{\"project name\":\"%s\", \"participants list\":\"%s\"}", getProjectName(),
        Arrays.toString(getParticipants().entrySet().stream()
            .map(entry -> String.format("\"id\":\"%d\", \"role\":\"%s\"", entry.getKey(), entry.getValue()))
            .toArray()));
  }

}
