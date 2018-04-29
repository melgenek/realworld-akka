package realworld.facade

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData, UserUpdateData}
import realworld.error.{LoginPasswordAuthError, PropertyError, TokenError}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): F[Either[PropertyError, UserData]]

  def login(loginData: LoginData): F[Either[LoginPasswordAuthError, UserData]]

  def getByEmail(email: String): F[Either[TokenError, UserData]]

  def updateUser(email: String, userUpdateData: UserUpdateData): F[Either[PropertyError, UserData]]

}

class UserFacadeImpl[F[_] : Monad](userService: UserService[F],
                                   tokenService: TokenService,
                                   registrationDataValidator: Validator[RegistrationData, F],
                                   userUpdateDataValidator: Validator[UserUpdateData, F]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): F[Either[PropertyError, UserData]] =
    (for {
      validUserData <- EitherT(registrationDataValidator.validate(registrationData))
      userToCreate = validUserData.into[User]
        .withFieldConst(_.bio, Option.empty[String])
        .withFieldConst(_.image, Option.empty[String])
        .transform
      registeredUser <- EitherT.right[PropertyError](userService.create(userToCreate))
    } yield convertToUserData(registeredUser)).value

  override def login(loginData: LoginData): F[Either[LoginPasswordAuthError, UserData]] =
    userService.login(loginData.email, loginData.password).map(_.map(convertToUserData))

  override def getByEmail(email: String): F[Either[TokenError, UserData]] =
    for {
      userOpt <- userService.findByEmail(email)
    } yield userOpt.map(_.asRight)
      .getOrElse(TokenError().asLeft)
      .map(convertToUserData)

  override def updateUser(email: String, userUpdateData: UserUpdateData): F[Either[PropertyError, UserData]] =
    for {
      user <- userService.getByEmail(email)
      updatedUser <- EitherT(userUpdateDataValidator.validate(userUpdateData)).semiflatMap { validData =>
        val userToUpdate: User = User(
          email = validData.email.getOrElse(user.email),
          username = validData.username.getOrElse(user.username),
          password = validData.password.getOrElse(user.password),
          bio = validData.bio.orElse(user.bio),
          image = validData.image.orElse(user.image)
        )
        userService.update(user.email, userToUpdate)
      }.value
    } yield updatedUser.map(convertToUserData)

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
