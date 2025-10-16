package com.personal.njtodo.EJBs.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.services.projections.UserAuthProjection;

@Repository
public interface UserSqlRepo extends JpaRepository<User, Long> {
  @Query("SELECT u FROM User u WHERE u.username=:username")
  UserAuthProjection getUserAuthByUsername(@Param("username") String username);

  @Query("SELECT u FROM User u WHERE u.username = :username")
  User getUserByUsername(@Param("username") String username);
}
