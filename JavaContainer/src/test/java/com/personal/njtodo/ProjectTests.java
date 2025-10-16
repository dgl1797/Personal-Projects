package com.personal.njtodo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.Assert;

import com.personal.njtodo.EJBs.entities.Project;
import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.repositories.mysql.ProjectSqlRepo;
import com.personal.njtodo.EJBs.repositories.mysql.UserSqlRepo;
import com.personal.njtodo.utilities.ResponseCompatibleException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectTests {
  @Autowired
  private ProjectSqlRepo projectSqlRepo;

  @Autowired
  private UserSqlRepo userSqlRepo;

  @Test
  public void injectedComponentsTest() {
    Assert.notNull(projectSqlRepo, "Couldn't load userRepo");
  }

  @Test
  public void correctProjectSaveTest() {
    try {
      User mockUser = new User("test.user@test.com", "test", "test.user");
      User savedUser = userSqlRepo.save(mockUser);

      Project newProject = new Project("test project", savedUser);
      Project savedProject = projectSqlRepo.save(newProject);
      System.out.println(savedProject);

      User retrievedUser = userSqlRepo.getUserByUsername(savedUser.getUsername());
      System.out.println(retrievedUser);
    } catch (ResponseCompatibleException e) {
      Assert.isTrue(false, "threw unexpected exception");
    }
  }

}
