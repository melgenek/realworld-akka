package realworld.validation

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.config.ActorModule
import realworld.data.RegistrationData
import realworld.model.User
import realworld.service.ServiceModule
import realworld.util.db.DBIOInstances

import scala.concurrent.Future

trait ValidationModule extends ServiceModule with ActorModule with FutureInstances with DBIOInstances {

  val registrationDataValidator: Validator[RegistrationData, User, Future] = wire[RegistrationDataValidator[Future]]

}
