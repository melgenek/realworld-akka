package realworld.facade

import cats.MonadError
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{RegistrationData, UserData}
import realworld.exception.PropertyException
import realworld.model.User
import realworld.service.{AuthService, UserService}
import realworld.util.ExceptionME
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def registerUser(registrationData: RegistrationData): F[UserData]

}

class UserFacadeImpl[F[_]](userService: UserService[F],
                           authService: AuthService,
                           registrationDataValidator: Validator[RegistrationData, User, F])
                          (implicit monadError: MonadError[F, Throwable]) extends UserFacade[F] {

  def registerUser(registrationData: RegistrationData): F[UserData] =
    for {
      userValidation <- registrationDataValidator.validate(registrationData)
      validUser <- userValidation
        .map(ExceptionME[F].pure)
        .valueOr(e => ExceptionME[F].raiseError(PropertyException(e)))
      registeredUser <- userService.create(validUser)
    } yield registeredUser.into[UserData]
      .withFieldComputed(_.token, u => authService.createTokenByEmail(u.email))
      .transform


}
