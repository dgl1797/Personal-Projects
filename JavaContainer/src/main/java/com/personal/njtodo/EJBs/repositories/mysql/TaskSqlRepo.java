package com.personal.njtodo.EJBs.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personal.njtodo.EJBs.entities.Task;

@Repository
public interface TaskSqlRepo extends JpaRepository<Task, Long> {

  @Query("SELECT t FROM Task t WHERE t.name=:taskName AND t.project=:projectID")
  Task getByNameAndProject(@Param("taskName") String taskName, @Param("projectID") Long projectID);

}
