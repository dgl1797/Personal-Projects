package com.personal.njtodo.endpoints.validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpStatus;

import com.personal.njtodo.endpoints.DTOs.ProjectCreationDTO;
import com.personal.njtodo.endpoints.DTOs.TaskCreationDTO;
import com.personal.njtodo.utilities.ResponseCompatibleException;

public class ProjectValidators {
  public static final void validateProjectCreationPayload(ProjectCreationDTO pcdto) throws ResponseCompatibleException {
    if (pcdto == null)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "body is mandatory")).logAndThrow();
    if (pcdto.getProjectName() == null || pcdto.getProjectName().isEmpty())
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "project name is required")).logAndThrow();
    if (pcdto.getParticipants() == null)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(),
          "List of participants is required, just keep it empty")).logAndThrow();
  }

  public static final void validateTaskCreationPayload(TaskCreationDTO tcdto, Long projectId)
      throws ResponseCompatibleException {
    if (tcdto == null)
      (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), "body is mandatory")).logAndThrow();
    Map<String, Boolean> conditionMap = new HashMap<>();
    conditionMap.put("name field is required and not empty in payload",
        tcdto.getTaskName() == null || tcdto.getTaskName() == "");
    conditionMap.put("projectId field is required in payload", projectId == null);
    conditionMap.put("assignees field is required in payload", tcdto.getAssignees() == null);
    for (Entry<String, Boolean> condition : conditionMap.entrySet()) {
      if (condition.getValue())
        (new ResponseCompatibleException(HttpStatus.BAD_REQUEST.value(), condition.getKey())).logAndThrow();
    }
  }
}
