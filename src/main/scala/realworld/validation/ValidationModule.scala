package realworld.validation

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.config.ActorModule
import realworld.dao.DaoModule
import realworld.data.RegistrationData
import realworld.model.User
import realworld.util.db.DBIOInstances
import slick.dbio.DBIO

import scala.concurrent.Future

trait ValidationModule extends DaoModule with ActorModule with FutureInstances with DBIOInstances {

  val registrationDataValidator: Validator[RegistrationData, User, Future] = wire[RegistrationDataValidator[Future, DBIO]]

}
