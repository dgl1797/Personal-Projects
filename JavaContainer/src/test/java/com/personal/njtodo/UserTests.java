package com.personal.njtodo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.Assert;

import com.personal.njtodo.EJBs.entities.User;
import com.personal.njtodo.EJBs.repositories.mysql.UserSqlRepo;
import com.personal.njtodo.endpoints.validators.AuthorizationValidators;
import com.personal.njtodo.utilities.ResponseCompatibleException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserTests {

  @Autowired
  private UserSqlRepo userRepo;

  @Test
  public void injectedComponentsTest() {
    Assert.notNull(userRepo, "Couldn't load userRepo");
  }

  @Test
  public void correctUserSaveTest() {
    try {
      User newUser = new User("test.user@test.com", "test", "test.user");

      User savedUser = userRepo.save(newUser);

      System.out.println(savedUser);

      Assert.notNull(savedUser, "Couldn't save user");
      Assert.isTrue(savedUser.getEmail().equals("test.user@test.com"), "saved wrong data");
    } catch (ResponseCompatibleException rce) {
      Assert.isTrue(false, "Threw exception on valid user information");
    }
  }

  @Test
  public void loginHeaderValidationTest() {
    try {
      AuthorizationValidators.validateLoginHeader("f.cantina:patAcchi12!a");
    } catch (ResponseCompatibleException rce) {
      Assert.isTrue(false, "Threw exception over valid authorization string");
    }
  }

}