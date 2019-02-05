package realworld.service

import cats.{Monad, ~>}
import realworld.dao.{RelationDao, UserDao}
import cats.implicits._
import scala.language.higherKinds

trait RelationService[F[_]] {

  def follows(followerEmail: String, followeeEmail: String): F[Boolean]

  def follow(followerEmail: String, followeeEmail: String): F[Unit]

  def unfollow(followerEmail: String, followeeEmail: String): F[Unit]

}

class RelationServiceImpl[F[_] : Monad, DB[_] : Monad](relationDao: RelationDao[DB],
                                                       userDao: UserDao[DB],
                                                       db: DB ~> F) extends RelationService[F] {

  override def follows(followerEmail: String, followeeEmail: String): F[Boolean] =
    db {
      relationDao.follows(followerEmail, followeeEmail)
    }

  override def follow(followerEmail: String, followeeEmail: String): F[Unit] =
    db {
      for {
        following <- relationDao.follows(followerEmail, followeeEmail)
        _ <- if (!following) changeRelation(followerEmail, followeeEmail, relationDao.follow)
        else Monad[DB].unit
      } yield ()
    }

  override def unfollow(followerEmail: String, followeeEmail: String): F[Unit] =
    db {
      changeRelation(followerEmail, followeeEmail, relationDao.unfollow)
    }

  private def changeRelation(followerEmail: String, followeeEmail: String, change: (Long, Long) => DB[Unit]): DB[Unit] =
    for {
      follower <- userDao.getByEmail(followerEmail)
      followee <- userDao.getByEmail(followeeEmail)
      _ <- change(follower.id, followee.id)
    } yield ()


}
