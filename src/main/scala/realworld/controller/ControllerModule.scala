package realworld.controller

import akka.http.scaladsl.server.{Directives, Route}
import com.softwaremill.macwire._
import realworld.facade.FacadeModule
import realworld.util.akka.Controller

trait ControllerModule extends FacadeModule with Directives {

  lazy val authController: UserController = wire[UserController]

  lazy val controllers: Set[Controller] = wireSet[Controller]

  lazy val routes: Route = pathPrefix("api") {
    controllers.foldLeft[Route](reject)(_ ~ _.route)
  }

}
