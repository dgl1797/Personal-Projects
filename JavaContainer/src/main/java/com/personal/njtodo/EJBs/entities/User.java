package com.personal.njtodo.EJBs.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.personal.njtodo.utilities.AccessManager;
import com.personal.njtodo.utilities.AuthInformation;
import com.personal.njtodo.utilities.ResponseCompatibleException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "user")
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Email(message = "invalid email format")
  @NotEmpty
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotEmpty
  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "premium", nullable = false)
  private Boolean premium;

  @NotEmpty
  @Column(name = "password", nullable = false)
  private String password;

  @NotEmpty
  @Column(name = "salt", nullable = false)
  private String salt;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Project> ownedProjects;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserParticipateProject> participates;

  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "assignees")
  private Set<Task> assignedTasks;

  public User() {}

  public User(String email, String password, String username, Boolean premium) throws ResponseCompatibleException {
    this.email = email;
    AuthInformation auth = AccessManager.encrypt(password);
    this.password = auth.getGeneratedPassword();
    this.salt = auth.getGeneratedSalt();
    this.username = username;
    this.premium = premium;
  }

  public User(String email, String password, String username) throws ResponseCompatibleException {
    this.email = email;
    AuthInformation auth = AccessManager.encrypt(password);
    this.password = auth.getGeneratedPassword();
    this.salt = auth.getGeneratedSalt();
    this.username = username;
    this.premium = false;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Boolean isPremium() {
    return this.premium;
  }

  public Boolean getPremium() {
    return this.premium;
  }

  public void setPremium(Boolean premium) {
    this.premium = premium;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSalt() {
    return this.salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public Set<Project> getOwnedProjects() {
    return this.ownedProjects == null ? new HashSet<>() : this.ownedProjects;
  }

  public Set<UserParticipateProject> getParticipations() {
    return this.participates == null ? new HashSet<>() : this.participates;
  }

  public void setParticipations(Set<UserParticipateProject> projects) {
    this.participates = projects;
  }

  public Set<Task> getAssignedTasks() {
    return this.assignedTasks;
  }

  public void setAssignedTasks(Set<Task> assignedTasks) {
    this.assignedTasks = assignedTasks;
  }

  @Override
  public String toString() {
    return String.format(
        "{\"id\":\"%d\", \"email\":\"%s\", \"password\":\"%s\", \"salt\":\"%s\", \"username\":\"%s\", \"premium\":\"%b\", \"ownedProjects\":\"%s\", \"participates\":\"%s\"}",
        getId(), getEmail(), getPassword(), getSalt(), getUsername(), getPremium(),
        Arrays
            .toString(getOwnedProjects().stream().map(p -> String.format("[%s]:%s", p.getId(), p.getName())).toArray()),
        Arrays.toString(getParticipations().stream()
            .map(p -> String.format("[%s]:%s - %s", p.getProject().getId(), p.getProject().getName(), p.getRole()))
            .toArray()));
  }

}
