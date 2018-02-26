package realworld.validation.entity

sealed trait UsernameValidation extends PropertyValidation {
  override def property: String = "username"
}

object AlreadyTakenUsername extends UsernameValidation with AlreadyTakenValidation

case class ShortUsername(size: Int) extends UsernameValidation with ShortLengthValidation

case class LongUsername(size: Int) extends UsernameValidation with LongLengthValidation


sealed trait PasswordValidation extends PropertyValidation {
  override def property: String = "password"
}

case class ShortPassword(size: Int) extends PasswordValidation with ShortLengthValidation

case class LongPassword(size: Int) extends PasswordValidation with LongLengthValidation


sealed trait EmailValidation extends PropertyValidation {
  override def property: String = "email"
}

object AlreadyTakenEmail extends EmailValidation with AlreadyTakenValidation

object InvalidEmail extends EmailValidation {
  override def message: String = "is invalid"
}
