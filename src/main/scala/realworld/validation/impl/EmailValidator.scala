package realworld.validation.impl

import cats.Monad
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import org.apache.commons.validator.routines
import realworld.service.UserService
import realworld.validation.AbstractValidator
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, InvalidEmail}

import scala.language.higherKinds

class EmailValidator[F[_] : Monad](userService: UserService[F]) extends AbstractValidator[String, F] {

  override def validateAndCollectErrors(email: String): F[ValidationResult[String]] =
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
    if (routines.EmailValidator.getInstance().isValid(email)) email.validNel
    else InvalidEmail.invalidNel

}
