package realworld.validation.impl

import cats.implicits._
import cats.{Id, Monad}
import realworld.data.UserUpdateData
import realworld.validation.AbstractValidator
import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

class UserUpdateDataValidator[F[_] : Monad](emailValidator: EmailValidator[F],
                                            usernameValidator: UsernameValidator[F],
                                            passwordValidator: PasswordValidator) extends AbstractValidator[UserUpdateData, F] {

  def validateAndCollectErrors(updateData: UserUpdateData): F[ValidationResult[UserUpdateData]] =
    for {
      emailV <- validatedField(updateData.email)(emailValidator.validateAndCollectErrors)
      usernameV <- validatedField(updateData.username)(usernameValidator.validateAndCollectErrors)
    } yield {
      val passwordV: ValidationResult[Option[String]] = validatedField[Id](updateData.password)(passwordValidator.validateAndCollectErrors)
      (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
        updateData.copy(
          email = email,
          username = username,
          password = password
        )
      }
    }

  private def validatedField[T[_] : Monad](field: Option[String])
                                          (validationFunction: String => T[ValidationResult[String]]): T[ValidationResult[Option[String]]] =
    field
      .map(fieldValue => validationFunction(fieldValue).map(_.map(Option(_))))
      .getOrElse(Monad[T].pure(Option.empty[String].validNel))

}
