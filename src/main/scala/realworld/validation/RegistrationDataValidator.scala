package realworld.validation

import cats.data.Validated._
import cats.implicits._
import cats.{Monad, ~>}
import org.apache.commons.validator.routines.EmailValidator
import realworld.dao.UserDao
import realworld.data.RegistrationData
import realworld.model.User
import realworld.validation.entity.PropertyValidation.ValidationResult
import realworld.validation.entity.{AlreadyTakenEmail, AlreadyTakenUsername, EmptyEmail, EmptyPassword, EmptyUsername, InvalidEmail, LongPassword, LongUsername, ShortPassword, ShortUsername}

import scala.language.higherKinds

class RegistrationDataValidator[F[_] : Monad, DB[_] : Monad](userDao: UserDao[DB],
                                                             db: DB ~> F) extends Validator[RegistrationData, User, F] {

  def validate(registrationData: RegistrationData): F[ValidationResult[User]] =
    db {
      for {
        emailV <- validateEmail(registrationData.email)
        usernameV <- validateUsername(registrationData.username)
        passwordV = validatePasswordFormat(registrationData.password)
      } yield {
        (emailV, usernameV, passwordV).mapN { case (email, username, password) =>
          User(email, username, password)
        }
      }
    }

  private[validation] def validateUsername(usernameOpt: Option[String]): DB[ValidationResult[String]] =
    validateUsernameFormat(usernameOpt) match {
      case Valid(username) =>
        userDao.findByUsername(username).map(userOpt =>
          userOpt
            .map(_ => AlreadyTakenUsername.invalidNel)
            .getOrElse(username.validNel)
        )
      case i@Invalid(_) => Monad[DB].pure(i)
    }

  private def validateUsernameFormat(usernameOpt: Option[String]): ValidationResult[String] =
    usernameOpt
      .map { username =>
        if (username.length < 1) ShortUsername(1).invalidNel
        else if (username.length > 20) LongUsername(20).invalidNel
        else username.validNel
      }
      .getOrElse(EmptyUsername.invalidNel)


  private[validation] def validatePasswordFormat(passwordOpt: Option[String]): ValidationResult[String] =
    passwordOpt
      .map { password =>
        if (password.length < 8) ShortPassword(8).invalidNel
        else if (password.length > 72) LongPassword(72).invalidNel
        else password.validNel
      }
      .getOrElse(EmptyPassword.invalidNel)

  private[validation] def validateEmail(emailOpt: Option[String]): DB[ValidationResult[String]] =
    validateEmailFormat(emailOpt) match {
      case Valid(email) =>
        userDao.findByEmail(email).map(userOpt =>
          userOpt
            .map(_ => AlreadyTakenEmail.invalidNel)
            .getOrElse(email.validNel)
        )
      case i@Invalid(_) => Monad[DB].pure(i)
    }

  private def validateEmailFormat(emailOpt: Option[String]): ValidationResult[String] =
    emailOpt
      .map { email =>
        if (EmailValidator.getInstance().isValid(email)) email.validNel
        else InvalidEmail.invalidNel
      }
      .getOrElse(EmptyEmail.invalidNel)


}
