package realworld.facade

import cats.Monad
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData}
import realworld.exception.{LoginPasswordAuthError, PropertyError, TokenError}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): F[Either[PropertyError, UserData]]

  def login(loginData: LoginData): F[Either[LoginPasswordAuthError, UserData]]

  def getByEmail(email: String): F[Either[TokenError, UserData]]

}

class UserFacadeImpl[F[_] : Monad](userService: UserService[F],
                                   tokenService: TokenService,
                                   registrationDataValidator: Validator[RegistrationData, User, F]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): F[Either[PropertyError, UserData]] =
    for {
      userValidation <- registrationDataValidator.validate(registrationData)
      registeredUser <- userValidation
        .map(validUser => register(validUser).map(_.asRight[PropertyError]))
        .valueOr(e => Monad[F].pure(PropertyError(e).asLeft[UserData]))
    } yield registeredUser

  private def register(user: User) =
    for {
      registeredUser <- userService.create(user)
    } yield convertToUserData(registeredUser)

  override def login(loginData: LoginData): F[Either[LoginPasswordAuthError, UserData]] =
    for {
      user <- userService.login(loginData.email, loginData.password)
    } yield user.map(convertToUserData)

  override def getByEmail(email: String): F[Either[TokenError, UserData]] =
    for {
      userOpt <- userService.findByEmail(email)
      userData <- userOpt
        .map(user => Monad[F].pure(convertToUserData(user).asRight[TokenError]))
        .getOrElse(Monad[F].pure(TokenError().asLeft[UserData]))
    } yield userData

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
