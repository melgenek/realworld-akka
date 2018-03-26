package realworld.facade

import cats.MonadError
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData}
import realworld.exception.{LoginPasswordAuthException, PropertyException, TokenException}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.util.ExceptionME
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): F[Either[PropertyException, UserData]]

  def login(loginData: LoginData): F[Either[LoginPasswordAuthException, UserData]]

  def getByEmail(email: String): F[Either[TokenException, UserData]]

}

class UserFacadeImpl[F[_]](userService: UserService[F],
                           tokenService: TokenService,
                           registrationDataValidator: Validator[RegistrationData, User, F])
                          (implicit monadError: MonadError[F, Throwable]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): F[Either[PropertyException, UserData]] =
    for {
      userValidation <- registrationDataValidator.validate(registrationData)
      registeredUser <- userValidation
        .map(validUser => register(validUser).map(_.asRight[PropertyException]))
        .valueOr(e => ExceptionME[F].pure(PropertyException(e).asLeft[UserData]))
    } yield registeredUser

  private def register(user: User) =
    for {
      registeredUser <- userService.create(user)
    } yield convertToUserData(registeredUser)

  override def login(loginData: LoginData): F[Either[LoginPasswordAuthException, UserData]] =
    for {
      user <- userService.login(loginData.email, loginData.password)
    } yield user.map(convertToUserData)

  override def getByEmail(email: String): F[Either[TokenException, UserData]] =
    for {
      userOpt <- userService.findByEmail(email)
      userData <- userOpt
        .map(user => ExceptionME[F].pure(convertToUserData(user).asRight[TokenException]))
        .getOrElse(ExceptionME[F].pure(TokenException().asLeft[UserData]))
    } yield userData

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
