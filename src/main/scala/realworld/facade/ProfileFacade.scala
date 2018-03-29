package realworld.facade

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import io.scalaland.chimney.dsl._
import realworld.data.ProfileData
import realworld.exception.NoProfileError
import realworld.model.User
import realworld.service.{RelationService, UserService}

import scala.language.higherKinds

trait ProfileFacade[F[_]] {

  def get(profileUsername: String, currentUserEmail: String): F[Either[NoProfileError, ProfileData]]

}

class ProfileFacadeImpl[F[_] : Monad](userService: UserService[F],
                                      relationService: RelationService[F]) extends ProfileFacade[F] {

  override def get(profileUsername: String, currentUserEmail: String): F[Either[NoProfileError, ProfileData]] =
    for {
      profileUserOpt <- userService.findByUsername(profileUsername)
      profileUser <- EitherT.fromOption[F](profileUserOpt, NoProfileError()).flatMapF { profileUser =>
        following(profileUser, currentUserEmail).map { following =>
          (profileUser, following).asRight[NoProfileError]
        }
      }.value
    } yield profileUser.map { case (user, following) =>
      user.into[ProfileData]
        .withFieldConst(_.following, following)
        .transform
    }

  private def following(profileUser: User, currentUserEmail: String): F[Boolean] =
    for {
      authenticatedUserOpt <- userService.findByEmail(currentUserEmail)
      following <- authenticatedUserOpt.map { authenticatedUser =>
        relationService.follows(authenticatedUser.email, profileUser.email)
      }.getOrElse(Monad[F].pure(false))
    } yield following

}

