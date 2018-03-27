package realworld.facade

import cats.Monad
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData, UserUpdateData}
import realworld.exception.{LoginPasswordAuthError, PropertyError, TokenError}
import realworld.model.User
import realworld.service.{HashService, TokenService, UserService}
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
                                   hashService: HashService,
                                   registrationDataValidator: Validator[RegistrationData, User, F],
                                   userUpdateDataValidator: Validator[UserUpdateData, UserUpdateData, F]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): F[Either[PropertyError, UserData]] =
    for {
      userValidation <- registrationDataValidator.validate(registrationData)
      registeredUser <- userValidation
        .map { validUser =>
          val userWithHashedPassword: User = validUser.copy(password = hashService.hashPassword(validUser.password))
          userService.create(userWithHashedPassword).map(_.asRight[PropertyError])
        }.valueOr(e => Monad[F].pure(PropertyError(e).asLeft[User]))
    } yield registeredUser.map(convertToUserData)

  override def login(loginData: LoginData): F[Either[LoginPasswordAuthError, UserData]] =
    for {
      userOpt <- userService.findByEmail(loginData.email)
    } yield {
      val r: Either[LoginPasswordAuthError, User] = userOpt.map { user =>
        if (hashService.isPasswordCorrect(loginData.password, user.password)) user.asRight
        else LoginPasswordAuthError().asLeft
      }.getOrElse(LoginPasswordAuthError().asLeft)
      r.map(convertToUserData)
    }

  override def getByEmail(email: String): F[Either[TokenError, UserData]] =
    for {
      userOpt <- userService.findByEmail(email)
    } yield userOpt.map(_.asRight)
      .getOrElse(TokenError().asLeft)
      .map(convertToUserData)

  override def updateUser(email: String, userUpdateData: UserUpdateData): F[Either[PropertyError, UserData]] =
    for {
      user <- userService.getByEmail(email)
      validUserUpdateData <- userUpdateDataValidator.validate(userUpdateData)
      updatedUser <- validUserUpdateData.map { validData =>
        val userToUpdate: User = user.copy(
          email = validData.email.getOrElse(user.email),
          username = validData.username.getOrElse(user.username),
          password = validData.password.map(hashService.hashPassword).getOrElse(user.password),
          bio = validData.bio.orElse(user.bio),
          image = validData.image.orElse(user.image)
        )
        userService.update(user.email, userToUpdate).map(_.asRight[PropertyError])
      }.valueOr(e => Monad[F].pure(PropertyError(e).asLeft[User]))
    } yield updatedUser.map(convertToUserData)

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
