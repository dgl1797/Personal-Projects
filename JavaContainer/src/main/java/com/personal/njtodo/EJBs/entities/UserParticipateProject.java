package com.personal.njtodo.EJBs.entities;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.personal.njtodo.EJBs.entities.support_objects.UserProjectId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "user_participate_project")
public class UserParticipateProject {

  // IMPORTANT: initializing the embedded key or hibernate won't be able to set the ids
  @EmbeddedId
  private UserProjectId id = new UserProjectId();

  @ManyToOne
  @JoinColumn(name = "uid", referencedColumnName = "id")
  @MapsId("userId") // mapping to the UserProjectId key name
  private User user;

  @ManyToOne
  @JoinColumn(name = "pjid", referencedColumnName = "id")
  @MapsId("projectId") // mapping to the UserProjectId key name
  private Project project;

  @NotEmpty
  @Column(name = "role", nullable = false)
  private String role;

  @Column(name = "added_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @CreationTimestamp
  private Timestamp added;

  public UserParticipateProject() {}

  public UserParticipateProject(User u, Project p, String role) {
    this.user = u;
    this.project = p;
    this.role = role;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Project getProject() {
    return this.project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Timestamp getAdded() {
    return this.added;
  }

  public void setAdded(Timestamp added) {
    this.added = added;
  }

  @Override
  public String toString() {
    return String.format("{\"user\":\"%s\", \"project\":\"%s\", \"role\":\"%s\", \"added\":\"%s\"}", getUser(),
        getProject(), getRole(), getAdded().toString());
  }

}
