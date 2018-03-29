package realworld.facade

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.service.ServiceModule
import realworld.validation.ValidationModule

import scala.concurrent.Future

trait FacadeModule extends ServiceModule with ValidationModule with FutureInstances {

  lazy val userFacade: UserFacade[Future] = wire[UserFacadeImpl[Future]]

  lazy val profileFacade: ProfileFacade[Future] = wire[ProfileFacadeImpl[Future]]

}
