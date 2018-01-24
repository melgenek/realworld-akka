package realworld.service

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.config.ActorModule
import realworld.dao.DaoModule
import realworld.util.db.DBIOInstances
import slick.dbio.DBIO

import scala.concurrent.Future

trait ServiceModule extends DaoModule with ActorModule with FutureInstances with DBIOInstances {

  lazy val authService: AuthService = wire[AuthServiceImpl]

  lazy val hashService: HashService = wire[BCryptHashService]

  lazy val userService: UserService[Future] = wire[UserServiceImpl[Future, DBIO]]

}
