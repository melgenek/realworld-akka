package realworld.validation

import cats.Id
import cats.implicits._
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.service.UserService
import realworld.util.TestData
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, AlreadyTakenUsername, InvalidEmail, LongPassword, LongUsername, ShortPassword, ShortUsername}

class RegistrationDataValidatorSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData.user

  "validateEmail" should "return valid email" in new Wiring {
    val res: ValidationResult[String] = validator.validateEmail("valid@email.com")

    res should be("valid@email.com".validNel)
  }

  it should "fail when email is invalid" in new Wiring {
    val res: ValidationResult[String] = validator.validateEmail("NOT_AN_EMAIL")

    res should be(InvalidEmail.invalidNel)
  }

  it should "fail when email has already been already taken" in new Wiring {
    when(userService.findByEmail(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validateEmail("valid@email.com")

    res should be(AlreadyTakenEmail.invalidNel)
  }

  "validatePasswordFormat" should "return valid password" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat("qwerty123")

    res should be("qwerty123".validNel)
  }

  it should "fail when password is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat("q")

    res should be(ShortPassword(8).invalidNel)
  }

  it should "fail when password is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat("qwerty123" * 10)

    res should be(LongPassword(72).invalidNel)
  }

  "validateUsername" should "return valid username" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername("username")

    res should be("username".validNel)
  }

  it should "fail when username is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername("")

    res should be(ShortUsername(1).invalidNel)
  }

  it should "fail when username is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername("username" * 5)

    res should be(LongUsername(20).invalidNel)
  }

  it should "fail when username has already been already taken" in new Wiring {
    when(userService.findByUsername(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validateUsername("username")

    res should be(AlreadyTakenUsername.invalidNel)
  }

  private trait Wiring {
    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.findByEmail(any())).thenReturn(None)
    when(userService.findByUsername(any())).thenReturn(None)

    val validator: RegistrationDataValidator[Id] = wire[RegistrationDataValidator[Id]]
  }

}
