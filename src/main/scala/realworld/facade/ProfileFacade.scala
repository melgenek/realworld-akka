package realworld.facade

import cats.Monad
import cats.data.{EitherT, OptionT}
import io.scalaland.chimney.dsl._
import realworld.data.ProfileData
import realworld.error.NoProfileError
import realworld.model.User
import realworld.service.{RelationService, UserService}

import scala.language.higherKinds

trait ProfileFacade[F[_]] {

  def get(profileUsername: String, currentUserEmail: String): EitherT[F, NoProfileError, ProfileData]

}

class ProfileFacadeImpl[F[_] : Monad](userService: UserService[F], relationService: RelationService[F]) extends ProfileFacade[F] {

  override def get(profileUsername: String, currentUserEmail: String): EitherT[F, NoProfileError, ProfileData] =
    for {
      profileUser <- EitherT.fromOptionF(userService.findByUsername(profileUsername), NoProfileError())
      following <- EitherT.liftF(following(profileUser, currentUserEmail))
    } yield profileUser.into[ProfileData]
      .withFieldConst(_.following, following)
      .transform

  private def following(profileUser: User, currentUserEmail: String): F[Boolean] =
    OptionT(userService.findByEmail(currentUserEmail))
      .semiflatMap { authenticatedUser => relationService.follows(authenticatedUser.email, profileUser.email) }
      .getOrElse(false)

}

