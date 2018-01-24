package realworld.config

import cats.~>
import com.softwaremill.macwire._
import realworld.util.db.DBIOTransformation
import slick.dbio.DBIO
import slick.jdbc.{H2Profile, JdbcBackend, JdbcProfile}

import scala.concurrent.Future

trait DbModule {

  lazy val profile: JdbcProfile = H2Profile

  lazy val db: JdbcBackend.DatabaseDef = JdbcBackend.Database.forConfig("realworld.db")

  lazy val dbioTransformation: DBIO ~> Future = wire[DBIOTransformation]

}
