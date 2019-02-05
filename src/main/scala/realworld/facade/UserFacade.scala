package realworld.facade

import cats.Monad
import cats.data.EitherT
import io.scalaland.chimney.dsl._
import realworld.data.{LoginData, RegistrationData, UserData, UserUpdateData}
import realworld.error.{LoginPasswordAuthError, PropertyError, TokenError}
import realworld.model.User
import realworld.service.{TokenService, UserService}
import realworld.validation.Validator

import scala.language.higherKinds

trait UserFacade[F[_]] {

  def register(registrationData: RegistrationData): EitherT[F, PropertyError, UserData]

  def login(loginData: LoginData): EitherT[F, LoginPasswordAuthError, UserData]

  def getByEmail(email: String): EitherT[F, TokenError, UserData]

  def updateUser(email: String, userUpdateData: UserUpdateData): EitherT[F, PropertyError, UserData]

}

class UserFacadeImpl[F[_] : Monad](userService: UserService[F],
                                   tokenService: TokenService,
                                   registrationDataValidator: Validator[RegistrationData, F],
                                   userUpdateDataValidator: Validator[UserUpdateData, F]) extends UserFacade[F] {

  def register(registrationData: RegistrationData): EitherT[F, PropertyError, UserData] =
    for {
      validUserData <- registrationDataValidator.validate(registrationData)
      userToCreate = validUserData.into[User]
        .withFieldConst(_.bio, Option.empty[String])
        .withFieldConst(_.image, Option.empty[String])
        .transform
      registeredUser <- EitherT.liftF(userService.create(userToCreate))
    } yield convertToUserData(registeredUser)

  override def login(loginData: LoginData): EitherT[F, LoginPasswordAuthError, UserData] =
    userService.login(loginData.email, loginData.password).map(convertToUserData)

  override def getByEmail(email: String): EitherT[F, TokenError, UserData] =
    EitherT.fromOptionF(userService.findByEmail(email), TokenError()).map(convertToUserData)

  def updateUser(email: String, userUpdateData: UserUpdateData): EitherT[F, PropertyError, UserData] =
    for {
      user <- EitherT.liftF(userService.getByEmail(email))
      validData <- userUpdateDataValidator.validate(userUpdateData)
      userToUpdate = User(
        email = validData.email.getOrElse(user.email),
        username = validData.username.getOrElse(user.username),
        password = validData.password.getOrElse(user.password),
        bio = validData.bio.orElse(user.bio),
        image = validData.image.orElse(user.image)
      )
      updatedUser <- EitherT.liftF(userService.update(user.email, userToUpdate))
    } yield convertToUserData(updatedUser)

  private def convertToUserData(user: User): UserData = user.into[UserData]
    .withFieldComputed(_.token, u => tokenService.createTokenByEmail(u.email))
    .transform

}
