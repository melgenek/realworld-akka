package realworld.service

import cats.{Monad, ~>}
import realworld.dao.RelationDao

import scala.language.higherKinds

trait RelationService[F[_]] {

  def follows(followerEmail: String, followeeEmail: String): F[Boolean]

}

class RelationServiceImpl[F[_] : Monad, DB[_] : Monad](relationDao: RelationDao[DB],
                                                       db: DB ~> F) extends RelationService[F] {

  override def follows(followerEmail: String, followeeEmail: String): F[Boolean] =
    db {
      relationDao.follows(followerEmail, followeeEmail)
    }

}
