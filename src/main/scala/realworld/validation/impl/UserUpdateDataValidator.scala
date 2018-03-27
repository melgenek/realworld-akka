package realworld.validation.impl

import cats.implicits._
import cats.{Id, Monad}
import realworld.data.UserUpdateData
import realworld.validation.Validator
import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

class UserUpdateDataValidator[F[_] : Monad](emailValidator: EmailValidator[F],
                                            usernameValidator: UsernameValidator[F],
                                            passwordValidator: PasswordValidator) extends Validator[UserUpdateData, UserUpdateData, F] {

  def validate(updateData: UserUpdateData): F[ValidationResult[UserUpdateData]] =
    for {
      emailV <- validatedField(updateData.email)(emailValidator.validate)
      usernameV <- validatedField(updateData.username)(usernameValidator.validate)
    } yield {
      val passwordV: ValidationResult[Option[String]] = validatedField[Id](updateData.password)(passwordValidator.validate)
      (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
        updateData.copy(
          email = email,
          username = username,
          password = password
        )
      }
    }

  private def validatedField[T[_] : Monad](field: Option[String])
                                          (f: String => T[ValidationResult[String]]): T[ValidationResult[Option[String]]] =
    field
      .map(e => f(e).map(_.map(Option(_))))
      .getOrElse(Monad[T].pure(None.asInstanceOf[Option[String]].validNel))

}
