package realworld.dao

import realworld.model.RelationModel
import slick.dbio._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait RelationDao[F[_]] {

  def follows(followerEmail: String, followeeEmail: String): F[Boolean]

  def follow(followerId: Long, followeeId: Long): F[Unit]

  def unfollow(followerId: Long, followeeId: Long): F[Unit]

}


class RelationDaoImpl(val profile: JdbcProfile)(implicit ec: ExecutionContext) extends RelationDao[DBIO] with RelationModel {

  import profile.api._

  override def follows(followerEmail: String, followeeEmail: String): DBIO[Boolean] =
    (for {
      relation <- relations
      follower <- relation.follower
      followee <- relation.followee
      if follower.email === followerEmail.bind
      if followee.email === followeeEmail.bind
    } yield relation)
      .result
      .headOption
      .map(_.isDefined)

  override def follow(followerId: Long, followeeId: Long): DBIO[Unit] =
    (relations += (followerId, followeeId)).map(_ => ())

  override def unfollow(followerId: Long, followeeId: Long): DBIO[Unit] =
    relations
      .filter(r => r.followerId === followerId.bind && r.followeeId === followeeId.bind)
      .delete
      .map(_ => ())

}