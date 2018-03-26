package realworld.facade

import cats.Id
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.softwaremill.macwire._
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.data.{LoginData, RegistrationData, UserData}
import realworld.exception.{LoginPasswordAuthError, PropertyError}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.util.{IdInstances, TestData}
import realworld.validation.Validator
import realworld.validation.entity.InvalidEmail

class UserFacadeSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "registerUser" should "generate token for valid user" in new Wiring {
    val result: Either[PropertyError, UserData] = facade.register(registrationData)

    result should equal(Right(UserData(
      Email,
      UserName,
      Token
    )))
  }

  it should "fail when registration data is invalid" in new Wiring {
    when(registrationDataValidator.validate(registrationData)).thenReturn(Invalid(NonEmptyList.of(InvalidEmail)))

    val result: Either[PropertyError, UserData] = facade.register(registrationData)

    result should equal(Left(PropertyError(NonEmptyList.of(InvalidEmail))))
  }

  "login" should "delegate to service" in new Wiring {
    val result: Either[LoginPasswordAuthError, UserData] = facade.login(LoginData(Email, Password))

    result should equal(Right(UserData(
      Email,
      UserName,
      Token
    )))
  }

  private trait Wiring extends IdInstances {
    val registrationData = RegistrationData(Email, UserName, Password)

    val registrationDataValidator: Validator[RegistrationData, User, Id] = mock[Validator[RegistrationData, User, Id]]
    when(registrationDataValidator.validate(registrationData)).thenReturn(Valid(user))

    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.create(user)).thenReturn(user)
    when(userService.login(Email, Password)).thenReturn(Right(user))

    val authService: TokenService = mock[TokenService]
    when(authService.createTokenByEmail(Email)).thenReturn(Token)

    val facade: UserFacade[Id] = wire[UserFacadeImpl[Id]]
  }


}
