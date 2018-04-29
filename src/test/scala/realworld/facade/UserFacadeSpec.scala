package realworld.facade

import cats.Id
import cats.data.NonEmptyList
import com.softwaremill.macwire._
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.data.{RegistrationData, UserData, UserUpdateData}
import realworld.error.PropertyError
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
    when(userService.create(User(Email, UserName, Password))).thenReturn(user)

    val result: Either[PropertyError, UserData] = facade.register(registrationData)

    result should equal(Right(UserData(
      Email,
      UserName,
      Token
    )))
  }

  it should "fail when registration data is invalid" in new Wiring {
    when(registrationDataValidator.validate(registrationData)).thenReturn(Left(PropertyError(NonEmptyList.of(InvalidEmail))))

    val result: Either[PropertyError, UserData] = facade.register(registrationData)

    result should equal(Left(PropertyError(NonEmptyList.of(InvalidEmail))))
  }

  private trait Wiring extends IdInstances {
    val registrationData = RegistrationData(Email, UserName, Password)

    val registrationDataValidator: Validator[RegistrationData, Id] = mock[Validator[RegistrationData, Id]]
    when(registrationDataValidator.validate(registrationData)).thenReturn(Right(registrationData))

    val userUpdateDataValidator: Validator[UserUpdateData, Id] = mock[Validator[UserUpdateData, Id]]

    val userService: UserService[Id] = mock[UserService[Id]]

    val authService: TokenService = mock[TokenService]
    when(authService.createTokenByEmail(Email)).thenReturn(Token)

    val facade: UserFacade[Id] = wire[UserFacadeImpl[Id]]
  }

}
