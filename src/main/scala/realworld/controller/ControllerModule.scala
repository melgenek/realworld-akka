package realworld.controller

import akka.http.scaladsl.server.{Directives, Route}
import com.softwaremill.macwire._
import realworld.service.ServiceModule
import realworld.util.Controller

trait ControllerModule extends ServiceModule with Directives {

  lazy val authController: AuthController = wire[AuthController]

  lazy val controllers: Set[Controller] = wireSet[Controller]

  lazy val routes: Route = pathPrefix("api") {
    controllers.foldLeft[Route](reject)(_ ~ _.route)
  }

}
