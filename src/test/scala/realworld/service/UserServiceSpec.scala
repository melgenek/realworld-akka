package realworld.service

import cats.Id
import com.softwaremill.macwire._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.data.{RegistrationData, UserData}
import realworld.model.User
import realworld.util.{IdTransformation, TestData}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "registerUser" should "create user from registration form" in new Wiring {
    val result: UserData = userService.registerUser(registrationData)

    val userCaptor: ArgumentCaptor[User] = ArgumentCaptor.forClass(classOf[User])
    verify(userDao).create(userCaptor.capture())

    val user: User = userCaptor.getValue
    user.email should equal(Email)
    user.username should equal(UserName)
    user.passwordHash should equal(PasswordHash)
  }

  it should "return created user" in new Wiring {
    val result: UserData = userService.registerUser(registrationData)

    result.username should equal(UserName)
    result.email should equal(Email)
    result.image should equal(Some(Image))
    result.bio should equal(Some(Bio))
  }

  it should "generate token for user" in new Wiring {
    when(authService.createTokenByEmail(any())).thenReturn(Token)

    val result: UserData = userService.registerUser(registrationData)

    result.token should equal(Token)
  }

  private trait Wiring extends IdTransformation {
    val registrationData = RegistrationData(UserName, Email, Password)

    val userDao: UserDao[Id] = mock[UserDao[Id]]
    val createdUser: User = User(Email, UserName, PasswordHash, Some(Bio), Some(Image))
    when(userDao.create(any())).thenReturn(createdUser)

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)

    val authService: AuthService = mock[AuthService]

    val userService: UserService[Id] = wire[UserServiceImpl[Id, Id]]
  }

}
