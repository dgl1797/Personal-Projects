package com.personal.njtodo.EJBs.entities.support_objects;

import java.io.Serializable;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserProjectId implements Serializable {

  private Long userId;
  private Long projectId;

  public UserProjectId() {}

  public UserProjectId(Long uid, Long pjid) {
    this.projectId = pjid;
    this.userId = uid;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long uid) {
    this.userId = uid;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public void setProjectId(Long pjid) {
    this.projectId = pjid;
  }

  @Override
  public boolean equals(Object o) {
    // reference matching
    if (this == o)
      return true;

    // class type missmatch || o is null and "this" is not since equals has been called
    if (o == null || getClass() != o.getClass())
      return false;

    // cast is now ensured to be possible, so we can value match the instances
    final UserProjectId that = (UserProjectId) o;
    return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getProjectId(), that.getProjectId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserId(), getProjectId());
  }

}
