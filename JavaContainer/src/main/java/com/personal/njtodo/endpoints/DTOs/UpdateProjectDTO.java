package com.personal.njtodo.endpoints.DTOs;

import java.util.Map;

public class UpdateProjectDTO {
  private Map<Long, String> newParticipants;

  public UpdateProjectDTO() {};

  public UpdateProjectDTO(Map<Long, String> newParticipants) {
    this.newParticipants = newParticipants;
  }

  public Map<Long, String> getNewParticipants() {
    return this.newParticipants;
  }

  public void setNewParticipants(Map<Long, String> newParticipants) {
    this.newParticipants = newParticipants;
  }

}
