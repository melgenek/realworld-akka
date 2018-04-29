package realworld.validation

import cats.Monad
import cats.implicits._
import realworld.error.PropertyError
import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

trait Validator[T, F[_]] {

  def validate(value: T): F[Either[PropertyError, T]]

  private[validation] def validateAndCollectErrors(value: T): F[ValidationResult[T]]

}

abstract class AbstractValidator[T, F[_] : Monad] extends Validator[T, F] {

  override def validate(value: T): F[Either[PropertyError, T]] =
    validateAndCollectErrors(value).map(_.toEither.left.map(PropertyError))

}
