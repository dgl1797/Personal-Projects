package com.personal.njtodo.EJBs.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personal.njtodo.EJBs.entities.UserParticipateProject;
import com.personal.njtodo.EJBs.entities.support_objects.UserProjectId;

@Repository
public interface UppSqlRepo extends JpaRepository<UserParticipateProject, UserProjectId> {

}
