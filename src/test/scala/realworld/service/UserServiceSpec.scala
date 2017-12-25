package realworld.service

import com.softwaremill.macwire._
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.data.{RegistrationData, UserData}
import realworld.model.User
import realworld.util.{SpecContext, SpecImplicits, TestData}

import scala.concurrent.Future

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar
    with ScalaFutures
    with SpecContext {

  import TestData._

  "registerUser" should "create user from registration form" in new Wiring {
    val resultFuture: Future[UserData] = userService.registerUser(registrationData)

    whenReady(resultFuture) { _ =>
      val userCaptor: ArgumentCaptor[User] = ArgumentCaptor.forClass(classOf[User])
      verify(userDao).create(userCaptor.capture())

      val user = userCaptor.getValue
      user.email should equal(Email)
      user.username should equal(UserName)
      user.passwordHash should equal(PasswordHash)
    }
  }

  it should "return created user" in new Wiring {
    val resultFuture: Future[UserData] = userService.registerUser(registrationData)

    whenReady(resultFuture) { userData =>
      userData.username should equal(UserName)
      userData.email should equal(Email)
      userData.image should equal(Some(Image))
      userData.bio should equal(Some(Bio))
    }
  }

  it should "generate token for user" in new Wiring {
    when(authService.createTokenByEmail(any())).thenReturn(Token)

    val resultFuture: Future[UserData] = userService.registerUser(registrationData)

    whenReady(resultFuture) { userData =>
      userData.token should equal(Token)
    }
  }

  private trait Wiring extends SpecImplicits {
    val registrationData = RegistrationData(UserName, Email, Password)

    val userDao: UserDao = mock[UserDao]
    val createdUser: User = User(Email, UserName, PasswordHash, Some(Bio), Some(Image))
    when(userDao.create(any())).thenReturnAsync(createdUser)

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)

    val authService: AuthService = mock[AuthService]

    val userService: UserService = wire[UserServiceImpl]
  }

}
