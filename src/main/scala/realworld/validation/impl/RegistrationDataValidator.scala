package realworld.validation.impl

import cats.Monad
import cats.data.Validated._
import cats.implicits._
import realworld.data.RegistrationData
import realworld.validation.AbstractValidator
import realworld.validation.entity.PropertyValidation.ValidationResult

import scala.language.higherKinds

class RegistrationDataValidator[F[_] : Monad](emailValidator: EmailValidator[F],
                                              usernameValidator: UsernameValidator[F],
                                              passwordValidator: PasswordValidator) extends AbstractValidator[RegistrationData, F] {

  def validateAndCollectErrors(registrationData: RegistrationData): F[ValidationResult[RegistrationData]] =
    for {
      emailV <- emailValidator.validateAndCollectErrors(registrationData.email)
      usernameV <- usernameValidator.validateAndCollectErrors(registrationData.username)
      passwordV = passwordValidator.validateAndCollectErrors(registrationData.password)
    } yield {
      (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
        RegistrationData(email = email, username = username, password = password)
      }
    }

}
