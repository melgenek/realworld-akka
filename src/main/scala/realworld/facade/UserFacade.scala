package realworld.facade

import cats.MonadError
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData}
import realworld.exception.{PropertyException, TokenException}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.util.ExceptionME
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): F[UserData]

  def login(loginData: LoginData): F[UserData]

  def getByEmail(email: String): F[UserData]

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
    } yield convertToUserData(registeredUser)

  override def login(loginData: LoginData): F[UserData] =
    for {
      user <- userService.login(loginData.email, loginData.password)
    } yield convertToUserData(user)

  override def getByEmail(email: String): F[UserData] =
    for {
      userOpt <- userService.findByEmail(email)
      user <- userOpt
        .map(ExceptionME[F].pure)
        .getOrElse(ExceptionME[F].raiseError(TokenException))
    } yield convertToUserData(user)

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
