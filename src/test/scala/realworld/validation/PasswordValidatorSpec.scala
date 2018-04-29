package realworld.validation

import cats.implicits._
import com.softwaremill.macwire._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{LongPassword, ShortPassword}
import realworld.validation.impl.PasswordValidator

class PasswordValidatorSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  "validatePasswordFormat" should "return valid password" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("qwerty123")

    res should be("qwerty123".validNel)
  }

  it should "fail when password is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("q")

    res should be(ShortPassword(8).invalidNel)
  }

  it should "fail when password is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("qwerty123" * 10)

    res should be(LongPassword(72).invalidNel)
  }

  private trait Wiring {
    val validator: PasswordValidator = wire[PasswordValidator]
  }

}
