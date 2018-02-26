package realworld.validation

import cats.Id
import cats.implicits._
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.util.{IdInstances, TestData}
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, AlreadyTakenUsername, EmptyEmail, EmptyPassword, EmptyUsername, InvalidEmail, LongPassword, LongUsername, ShortPassword, ShortUsername}

class RegistrationDataValidatorSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData.user

  "validateEmail" should "return valid email" in new Wiring {
    val res: ValidationResult[String] = validator.validateEmail(Some("valid@email.com"))

    res should be("valid@email.com".validNel)
  }

  it should "fail when email is invalid" in new Wiring {
    val res: ValidationResult[String] = validator.validateEmail(Some("NOT_AN_EMAIL"))

    res should be(InvalidEmail.invalidNel)
  }

  it should "fail when email is empty" in new Wiring {
    val res: ValidationResult[String] = validator.validateEmail(None)

    res should be(EmptyEmail.invalidNel)
  }

  it should "fail when email has already been already taken" in new Wiring {
    when(userDao.findByEmail(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validateEmail(Some("valid@email.com"))

    res should be(AlreadyTakenEmail.invalidNel)
  }

  "validatePasswordFormat" should "return valid password" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat(Some("qwerty123"))

    res should be("qwerty123".validNel)
  }

  it should "fail when password is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat(Some("q"))

    res should be(ShortPassword(8).invalidNel)
  }

  it should "fail when password is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat(Some("qwerty123" * 10))

    res should be(LongPassword(72).invalidNel)
  }

  it should "fail when password is empty" in new Wiring {
    val res: ValidationResult[String] = validator.validatePasswordFormat(None)

    res should be(EmptyPassword.invalidNel)
  }

  "validateUsername" should "return valid username" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername(Some("username"))

    res should be("username".validNel)
  }

  it should "fail when username is too short" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername(Some(""))

    res should be(ShortUsername(1).invalidNel)
  }

  it should "fail when username is too long" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername(Some("username" * 5))

    res should be(LongUsername(20).invalidNel)
  }

  it should "fail when username is empty" in new Wiring {
    val res: ValidationResult[String] = validator.validateUsername(None)

    res should be(EmptyUsername.invalidNel)
  }

  it should "fail when username has already been already taken" in new Wiring {
    when(userDao.findByUsername(any())).thenReturn(Some(user))

    val res: ValidationResult[String] = validator.validateUsername(Some("username"))

    res should be(AlreadyTakenUsername.invalidNel)
  }

  private trait Wiring extends IdInstances {
    val userDao: UserDao[Id] = mock[UserDao[Id]]
    when(userDao.findByEmail(any())).thenReturn(None)
    when(userDao.findByUsername(any())).thenReturn(None)

    val validator: RegistrationDataValidator[Id, Id] = wire[RegistrationDataValidator[Id, Id]]
  }

}
