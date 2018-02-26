package realworld.facade

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.service.ServiceModule
import realworld.validation.ValidationModule

import scala.concurrent.Future

class FacadeModule extends ServiceModule with ValidationModule with FutureInstances {

  val userFacade: UserFacade[Future] = wire[UserFacadeImpl[Future]]

}
