package realworld.service

import cats.Id
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.error.LoginPasswordAuthError
import realworld.model.{IdRecord, User}
import realworld.util.{IdInstances, TestData}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "login" should "return user when password is correct" in new Wiring {
    val result: Either[LoginPasswordAuthError, User] = service.login(Email, Password).value

    result should equal(Right(user))
  }

  it should "fail when password is not correct" in new Wiring {
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(false)

    val result: Either[LoginPasswordAuthError, User] = service.login(Email, Password).value

    result should be(Left(LoginPasswordAuthError()))
  }

  it should "fail when no user is found" in new Wiring {
    when(userDao.findByEmail(any())).thenReturn(None)

    val result: Either[LoginPasswordAuthError, User] = service.login(Email, Password).value

    result should be(Left(LoginPasswordAuthError()))
  }

  private trait Wiring extends IdInstances {
    val userDao: UserDao[Id] = mock[UserDao[Id]]
    val userRecord = IdRecord(0L, user)
    when(userDao.create(any())).thenReturn(userRecord)
    when(userDao.findByEmail(any())).thenReturn(Some(userRecord))

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(true)

    val service: UserService[Id] = wire[UserServiceImpl[Id, Id]]
  }

}
