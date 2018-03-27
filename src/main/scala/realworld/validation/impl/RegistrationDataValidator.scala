package realworld.validation.impl

import cats.Monad
import cats.data.Validated._
import cats.implicits._
import realworld.data.RegistrationData
import realworld.model.User
import realworld.validation.Validator
import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

class RegistrationDataValidator[F[_] : Monad](emailValidator: EmailValidator[F],
                                              usernameValidator: UsernameValidator[F],
                                              passwordValidator: PasswordValidator) extends Validator[RegistrationData, User, F] {

  def validate(registrationData: RegistrationData): F[ValidationResult[User]] =
    for {
      emailV <- emailValidator.validate(registrationData.email)
      usernameV <- usernameValidator.validate(registrationData.username)
      passwordV = passwordValidator.validate(registrationData.password)
    } yield {
      (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
        User(email = email, username = username, password = password)
      }
    }

}
