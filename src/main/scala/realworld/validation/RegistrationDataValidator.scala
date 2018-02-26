package realworld.validation

import cats.Monad
import cats.data.Validated._
import cats.implicits._
import org.apache.commons.validator.routines.EmailValidator
import realworld.data.RegistrationData
import realworld.model.User
import realworld.service.UserService
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, AlreadyTakenUsername, InvalidEmail, LongPassword, LongUsername, ShortPassword, ShortUsername}

import scala.language.higherKinds

class RegistrationDataValidator[F[_] : Monad](userService: UserService[F]) extends Validator[RegistrationData, User, F] {

  def validate(registrationData: RegistrationData): F[ValidationResult[User]] =
    for {
      emailV <- validateEmail(registrationData.email)
      usernameV <- validateUsername(registrationData.username)
      passwordV = validatePasswordFormat(registrationData.password)
    } yield {
      (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
        User(email = email, username = username, password = password)
      }
    }

  private[validation] def validateUsername(username: String): F[ValidationResult[String]] =
    validateUsernameFormat(username) match {
      case Valid(validUsername) =>
        userService.findByUsername(validUsername).map(userOpt =>
          userOpt
            .map(_ => AlreadyTakenUsername.invalidNel)
            .getOrElse(validUsername.validNel)
        )
      case i@Invalid(_) => Monad[F].pure(i)
    }

  private def validateUsernameFormat(username: String): ValidationResult[String] =
    if (username.length < 1) ShortUsername(1).invalidNel
    else if (username.length > 20) LongUsername(20).invalidNel
    else username.validNel

  private[validation] def validatePasswordFormat(password: String): ValidationResult[String] =
    if (password.length < 8) ShortPassword(8).invalidNel
    else if (password.length > 72) LongPassword(72).invalidNel
    else password.validNel


  private[validation] def validateEmail(email: String): F[ValidationResult[String]] =
    validateEmailFormat(email) match {
      case Valid(validEmail) =>
        userService.findByEmail(validEmail).map { userOpt =>
          userOpt
            .map(_ => AlreadyTakenEmail.invalidNel)
            .getOrElse(validEmail.validNel)
        }
      case i@Invalid(_) => Monad[F].pure(i)
    }

  private def validateEmailFormat(email: String): ValidationResult[String] =
    if (EmailValidator.getInstance().isValid(email)) email.validNel
    else InvalidEmail.invalidNel

}
