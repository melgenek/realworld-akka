package realworld.validation.impl

import cats.Monad
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import realworld.service.UserService
import realworld.validation.AbstractValidator
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenUsername, LongUsername, ShortUsername}

import scala.language.higherKinds

class UsernameValidator[F[_] : Monad](userService: UserService[F]) extends AbstractValidator[String, F] {

  override def validateAndCollectErrors(username: String): F[ValidationResult[String]] =
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

}
