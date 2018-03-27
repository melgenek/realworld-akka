package realworld.validation.impl

import cats.Id
import cats.implicits._
import realworld.validation.Validator
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{LongPassword, ShortPassword}

class PasswordValidator extends Validator[String, String, Id] {

  override def validate(password: String): ValidationResult[String] =
    if (password.length < 8) ShortPassword(8).invalidNel
    else if (password.length > 72) LongPassword(72).invalidNel
    else password.validNel

}
