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
import realworld.validation.entity.{AlreadyTakenUsername, LongUsername, ShortUsername}
import realworld.validation.impl.UsernameValidator

class UsernameValidatorSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  "validateUsername" should "return valid username" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("username")

    res should be("username".validNel)
  }

  it should "fail when username is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("")

    res should be(ShortUsername(1).invalidNel)
  }

  it should "fail when username is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validateAndCollectErrors("username" * 5)

    res should be(LongUsername(20).invalidNel)
  }

  it should "fail when username has already been already taken" in new Wiring {
    when(userService.findByUsername(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validateAndCollectErrors("username")

    res should be(AlreadyTakenUsername.invalidNel)
  }

  private trait Wiring {
    val userService: UserService[Id] = mock[UserService[Id]]
    when(userService.findByUsername(any())).thenReturn(None)

    val validator: UsernameValidator[Id] = wire[UsernameValidator[Id]]
  }

}
