package realworld.facade

import cats.MonadError
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData}
import realworld.exception.PropertyException
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.util.ExceptionME
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): F[UserData]

  def login(loginData: LoginData): F[UserData]

}

class UserFacadeImpl[F[_]](userService: UserService[F],
                           tokenService: TokenService,
                           registrationDataValidator: Validator[RegistrationData, User, F])
                          (implicit monadError: MonadError[F, Throwable]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): F[UserData] =
    for {
      userValidation <- registrationDataValidator.validate(registrationData)
      validUser <- userValidation
        .map(ExceptionME[F].pure)
        .valueOr(e => ExceptionME[F].raiseError(PropertyException(e)))
      registeredUser <- userService.create(validUser)
    } yield convertToUser(registeredUser)

  override def login(loginData: LoginData): F[UserData] =
    for {
      user <- userService.login(loginData.email, loginData.password)
    } yield convertToUser(user)

  private def convertToUser(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform


}
