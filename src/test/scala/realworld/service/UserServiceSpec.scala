package realworld.service

import cats.Id
import com.softwaremill.macwire._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.exception.{AuthException, LoginPasswordAuthException}
import realworld.model.{IdRecord, User}
import realworld.util.{IdInstances, TestData}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "create" should "hash password and create user using dao" in new Wiring {
    service.create(user)

    val userCaptor: ArgumentCaptor[User] = ArgumentCaptor.forClass(classOf[User])
    verify(userDao).create(userCaptor.capture())

    val result: User = userCaptor.getValue
    result should equal(User(Email, UserName, PasswordHash))
  }

  it should "return created user" in new Wiring {
    val result: User = service.create(user)

    result should equal(createdUser)
  }

  "login" should "return user when password is correct" in new Wiring {
    val result: User = service.login(Email, Password)

    result should equal(createdUser)
  }

  it should "fail when password is not correct" in new Wiring {
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(false)

    val e: AuthException = intercept[AuthException] {
      service.login(Email, Password)
    }

    e should be(LoginPasswordAuthException)
  }

  it should "fail when no user is found" in new Wiring {
    when(userDao.findByEmail(any())).thenReturn(None)

    val e: AuthException = intercept[AuthException] {
      service.login(Email, Password)
    }

    e should be(LoginPasswordAuthException)
  }

  private trait Wiring extends IdInstances {
    val userDao: UserDao[Id] = mock[UserDao[Id]]
    val createdUser: User = User(Email, UserName, PasswordHash, Some(Bio), Some(Image))
    val userRecord = IdRecord(0L, createdUser)
    when(userDao.create(any())).thenReturn(userRecord)
    when(userDao.findByEmail(any())).thenReturn(Some(userRecord))

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(true)

    val service: UserService[Id] = wire[UserServiceImpl[Id, Id]]
  }

}
