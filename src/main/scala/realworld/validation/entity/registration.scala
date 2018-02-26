package realworld.validation.entity

sealed trait UsernameValidation extends PropertyValidation {
  override def property: String = "username"
}

object EmptyUsername extends UsernameValidation with EmptyPropertyValidation

object AlreadyTakenUsername extends UsernameValidation with AlreadyTakenValidation

case class ShortUsername(size: Int) extends UsernameValidation with ShortLengthValidation

case class LongUsername(size: Int) extends UsernameValidation with LongLengthValidation


sealed trait PasswordValidation extends PropertyValidation {
  override def property: String = "password"
}

object EmptyPassword extends PasswordValidation with EmptyPropertyValidation

case class ShortPassword(size: Int) extends PasswordValidation with ShortLengthValidation

case class LongPassword(size: Int) extends PasswordValidation with LongLengthValidation


sealed trait EmailValidation extends PropertyValidation {
  override def property: String = "email"
}

object EmptyEmail extends EmailValidation with EmptyPropertyValidation

object AlreadyTakenEmail extends EmailValidation with AlreadyTakenValidation

object InvalidEmail extends EmailValidation {
  override def message: String = "is invalid"
}
