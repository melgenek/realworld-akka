package realworld.validation.entity

import cats.data.ValidatedNel

trait PropertyValidation {
  def property: String

  def message: String
}

object PropertyValidation {
  type ValidationResult[A] = ValidatedNel[PropertyValidation, A]
}

trait EmptyPropertyValidation extends PropertyValidation {
  override def message: String = "can't be blank"
}

trait AlreadyTakenValidation extends PropertyValidation {
  override def message: String = "has already been taken"
}

trait LengthValidation extends PropertyValidation {
  def size: Int
}

trait ShortLengthValidation extends LengthValidation {
  override def message: String = s"is too short (minimum is $size character)"
}

trait LongLengthValidation extends LengthValidation {
  override def message: String = s"is too long (maximum is $size characters)"
}
