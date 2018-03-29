package realworld.dao

import realworld.model.RelationModel
import slick.dbio._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait RelationDao[F[_]] {

  def follows(followerEmail: String, followeeEmail: String): F[Boolean]

}


class RelationDaoImpl(val profile: JdbcProfile)(implicit ec: ExecutionContext) extends RelationDao[DBIO] with RelationModel {

  import profile.api._

  override def follows(followerEmail: String, followeeEmail: String): DBIO[Boolean] =
    (for {
      relation <- relations
      follower <- relation.follower
      followee <- relation.followee
      if follower.email === followerEmail
      if followee.email === followeeEmail
    } yield relation)
      .result
      .headOption
      .map(_.isDefined)

}