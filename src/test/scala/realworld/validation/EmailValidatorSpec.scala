package realworld.validation

import cats.Id
import cats.implicits._
import com.softwaremill.macwire.wire
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.service.UserService
import realworld.util.TestData.user
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, InvalidEmail}
import realworld.validation.impl.EmailValidator

class EmailValidatorSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  "validateEmail" should "return valid email" in new Wiring {
    val res: ValidationResult[String] = validator.validate("valid@email.com")

    res should be("valid@email.com".validNel)
  }

  it should "fail when email is invalid" in new Wiring {
    val res: ValidationResult[String] = validator.validate("NOT_AN_EMAIL")

    res should be(InvalidEmail.invalidNel)
  }

  it should "fail when email has already been already taken" in new Wiring {
    when(userService.findByEmail(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validate("valid@email.com")

    res should be(AlreadyTakenEmail.invalidNel)
  }

  private trait Wiring {
    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.findByEmail(any())).thenReturn(None)

    val validator: EmailValidator[Id] = wire[EmailValidator[Id]]
  }

}
