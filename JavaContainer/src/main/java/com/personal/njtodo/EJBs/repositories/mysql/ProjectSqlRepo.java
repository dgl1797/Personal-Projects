package com.personal.njtodo.EJBs.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personal.njtodo.EJBs.entities.Project;

@Repository
public interface ProjectSqlRepo extends JpaRepository<Project, Long> {

  @Query("SELECT p FROM Project p WHERE p.owner=:owner AND p.name=:name")
  public Project getByNameAndOwner(@Param("owner") String owner, @Param("name") String name);

}
