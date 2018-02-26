package realworld.validation

import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

trait Validator[IN, OUT, F[_]] {
  def validate(value: IN): F[ValidationResult[OUT]]
}
