package realworld.validation.impl

import cats.Id
import cats.implicits._
import realworld.validation.AbstractValidator
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{LongPassword, ShortPassword}

class PasswordValidator extends AbstractValidator[String, Id] {

  override def validateAndCollectErrors(password: String): ValidationResult[String] =
    if (password.length < 8) ShortPassword(8).invalidNel
    else if (password.length > 72) LongPassword(72).invalidNel
    else password.validNel

}
