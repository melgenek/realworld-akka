package realworld.facade

import cats.Id
import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import com.softwaremill.macwire._
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.data.{RegistrationData, UserData}
import realworld.exception.PropertyException
import realworld.model.User
import realworld.service.{AuthService, UserService}
import realworld.util.{IdInstances, TestData}
import realworld.validation.Validator
import realworld.validation.entity.EmptyEmail

class UserFacadeSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "registerUser" should "generate token for valid user" in new Wiring {
    val result: UserData = facade.registerUser(registrationData)

    result should equal(UserData(
      Email,
      UserName,
      Token
    ))
  }

  it should "fail when registration data is invalid" in new Wiring {
    when(registrationDataValidator.validate(registrationData)).thenReturn(Invalid(NonEmptyList.of(EmptyEmail)))

    val e: PropertyException = intercept[PropertyException] {
      facade.registerUser(registrationData)
    }

    e should equal(PropertyException(NonEmptyList.of(EmptyEmail)))
  }


  private trait Wiring extends IdInstances {
    val registrationData = RegistrationData(Some(Email), Some(UserName), Some(Password))

    val registrationDataValidator: Validator[RegistrationData, User, Id] = mock[Validator[RegistrationData, User, Id]]
    when(registrationDataValidator.validate(registrationData)).thenReturn(Valid(user))

    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.create(user)).thenReturn(user)

    val authService: AuthService = mock[AuthService]
    when(authService.createTokenByEmail(Email)).thenReturn(Token)


    val facade: UserFacade[Id] = wire[UserFacadeImpl[Id]]

  }


}
