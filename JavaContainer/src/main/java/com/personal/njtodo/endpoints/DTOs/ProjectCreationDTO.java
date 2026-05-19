package com.personal.njtodo.endpoints.DTOs;

import java.util.Arrays;
import java.util.Map;

public class ProjectCreationDTO {
  private String projectName;
  private Map<String, String> participants;

  public ProjectCreationDTO(String pname, Map<String, String> pids) {
    this.projectName = pname;
    this.participants = pids;
  }

  public String getProjectName() {
    return this.projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public Map<String, String> getParticipants() {
    return this.participants;
  }

  public void setParticipants(Map<String, String> participants) {
    this.participants = participants;
  }

  @Override
  public String toString() {
    return String.format("{\"project name\":\"%s\", \"participants list\":\"%s\"}", getProjectName(),
        Arrays.toString(getParticipants().entrySet().stream()
            .map(entry -> String.format("\"username\":\"%s\", \"role\":\"%s\"", entry.getKey(), entry.getValue()))
            .toArray()));
  }

}