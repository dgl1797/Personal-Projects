package com.personal.njtodo.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.personal.njtodo.EJBs.entities.Task;
import com.personal.njtodo.EJBs.repositories.mysql.TaskSqlRepo;
import com.personal.njtodo.EJBs.services.ProjectService;
import com.personal.njtodo.EJBs.services.TaskService;
import com.personal.njtodo.endpoints.DTOs.FullTaskDTO;
import com.personal.njtodo.endpoints.DTOs.ProjectCreationDTO;
import com.personal.njtodo.endpoints.DTOs.ProjectDTO;
import com.personal.njtodo.endpoints.DTOs.TaskCreationDTO;
import com.personal.njtodo.endpoints.DTOs.TaskUpdateDTO;
import com.personal.njtodo.endpoints.DTOs.UpdateProjectDTO;
import com.personal.njtodo.endpoints.validators.ProjectValidators;
import com.personal.njtodo.filters.name_bindings.AuthenticateToken;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
@Path("/projects")
public class ProjectEndpoints {

  private final ProjectService projectService;
  private final TaskService taskService;

  public ProjectEndpoints(@Autowired ProjectService projectService, @Autowired TaskService taskService) {
    this.projectService = projectService;
    this.taskService = taskService;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response postProject(ProjectCreationDTO newProject, @HeaderParam("username") String requestingUser,
      @HeaderParam("user-type") String title) {
    try {
      if (!title.toLowerCase().equals("premium"))
        throw new ResponseCompatibleException(HttpStatus.UNAUTHORIZED.value(),
            "Only premium users are allowed to create projects");
      ProjectValidators.validateProjectCreationPayload(newProject);
      Long storedId = projectService.save(newProject.getProjectName(), requestingUser, newProject.getParticipants());
      return Response.status(200).entity(storedId).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response getProject(@PathParam("projectId") Long projectId, @HeaderParam("username") String requestingUser) {
    try {
      projectService.checkAccess(projectId, requestingUser);
      ProjectDTO result = projectService.getFullProjectById(projectId, requestingUser);
      return Response.status(200).entity(result).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response updateProjectParticipants(UpdateProjectDTO payload, @PathParam("projectId") Long projectId,
      @HeaderParam("username") String requestingUser) throws ResponseCompatibleException {
    try {
      projectService.checkAppartenence(requestingUser, projectId);
      projectService.appendParticipants(projectId, payload.getNewParticipants());
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}/users/{userId}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response removeUserFromProject(@PathParam("projectId") Long projectId, @PathParam("userId") Long userId,
      @HeaderParam("username") String requestingUser) {
    try {
      projectService.checkAccess(projectId, requestingUser);
      projectService.removeUserFromProject(projectId, userId);
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getStatus()).build();
    }
  }

  @Path("/{projectId}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response deleteProject(@PathParam("projectId") Long projectId,
      @HeaderParam("username") String requestingUser) {
    // TODO: test
    try {
      projectService.checkAppartenence(requestingUser, projectId);
      projectService.delete(projectId);
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}/tasks")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response postTaskToProject(TaskCreationDTO payload, @PathParam("projectId") Long projectId,
      @HeaderParam("username") String requestingUser) {
    try {
      ProjectValidators.validateTaskCreationPayload(payload, projectId);
      projectService.checkAppartenence(requestingUser, projectId);
      Long storedId = taskService.save(payload, projectId);
      return Response.status(200).entity(storedId).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}/tasks/{taskId}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response deleteTask(@PathParam("projectId") Long projectId, @PathParam("taskId") Long taskId,
      @HeaderParam("username") String requestingUser) {

    try {
      projectService.checkAppartenence(requestingUser, projectId);
      taskService.delete(projectId, taskId);
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }

  @Path("/{projectId}/tasks/{taskId}")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response updateTask(TaskUpdateDTO payload, @PathParam("projectId") Long projectId,
      @PathParam("taskId") Long taskId, @HeaderParam("username") String requestingUser) {

    try {
      // Allow project owner OR task assignees to update task state
      boolean isOwner = false;
      try {
        projectService.checkAppartenence(requestingUser, projectId);
        isOwner = true;
      } catch (ResponseCompatibleException e) {
        // Not the owner, check if assignee
        Task task = taskService.getFullById(taskId);
        boolean isAssignee = task.getAssignees().stream()
            .anyMatch(u -> u.getUsername().equals(requestingUser));
        if (!isAssignee)
          throw e;
      }
      taskService.updateService(taskId, projectId, requestingUser, payload);
      return Response.status(200).entity("ok").build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }

  }

  @Path("/{projectId}/tasks/{taskId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @AuthenticateToken
  public Response getTask(@PathParam("projectId") Long projectId, @PathParam("taskId") Long taskId)
      throws ResponseCompatibleException {
    try {
      taskService.checkAppartenence(taskId, projectId);
      FullTaskDTO fulltask = taskService.getFullById(taskId);
      return Response.status(200).entity(fulltask).build();
    } catch (ResponseCompatibleException rce) {
      return Response.status(rce.getStatus()).entity(rce.getMessage()).build();
    }
  }
}
