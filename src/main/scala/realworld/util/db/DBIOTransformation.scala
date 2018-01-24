package realworld.util.db

import cats.~>
import slick.dbio._
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.Future

class DBIOTransformation(val profile: JdbcProfile, db: JdbcBackend.Database) extends (DBIO ~> Future) {

  import profile.api._

  override def apply[A](dbio: DBIO[A]): Future[A] = db.run(dbio.transactionally)

}
