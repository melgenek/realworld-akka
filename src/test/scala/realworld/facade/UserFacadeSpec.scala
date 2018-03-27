package realworld.facade

import cats.Id
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.data.{LoginData, RegistrationData, UserData, UserUpdateData}
import realworld.exception.{LoginPasswordAuthError, PropertyError}
import realworld.model.User
import realworld.service.{HashService, TokenService, UserService}
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

  "login" should "return user when password is correct" in new Wiring {
    val result: Either[LoginPasswordAuthError, UserData] = facade.login(LoginData(Email, Password))

    result should equal(Right(UserData(
      Email,
      UserName,
      Token
    )))
  }

  it should "fail when password is not correct" in new Wiring {
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(false)

    val result: Either[LoginPasswordAuthError, UserData] = facade.login(LoginData(Email, Password))

    result should be(Left(LoginPasswordAuthError()))
  }

  it should "fail when no user is found" in new Wiring {
    when(userService.findByEmail(any())).thenReturn(None)

    val result: Either[LoginPasswordAuthError, UserData] = facade.login(LoginData(Email, Password))

    result should be(Left(LoginPasswordAuthError()))
  }

  private trait Wiring extends IdInstances {
    val registrationData = RegistrationData(Email, UserName, Password)
    val userWithHashedPassword: User = user.copy(password = PasswordHash)

    val registrationDataValidator: Validator[RegistrationData, User, Id] = mock[Validator[RegistrationData, User, Id]]
    when(registrationDataValidator.validate(registrationData)).thenReturn(Valid(user))

    val userUpdateDataValidator: Validator[UserUpdateData, UserUpdateData, Id] = mock[Validator[UserUpdateData, UserUpdateData, Id]]

    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.create(userWithHashedPassword)).thenReturn(userWithHashedPassword)
    when(userService.findByEmail(Email)).thenReturn(Some(userWithHashedPassword))

    val authService: TokenService = mock[TokenService]
    when(authService.createTokenByEmail(Email)).thenReturn(Token)

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)
    when(hashService.isPasswordCorrect(Password, PasswordHash)).thenReturn(true)

    val facade: UserFacade[Id] = wire[UserFacadeImpl[Id]]
  }

}
