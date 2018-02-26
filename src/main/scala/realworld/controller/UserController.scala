package realworld.controller

import akka.http.scaladsl.server.Route
import realworld.data.{RegistrationData, UserJsonProtocol}
import realworld.facade.UserFacade
import realworld.util.Controller

import scala.concurrent.Future

class UserController(userFacade: UserFacade[Future]) extends Controller with UserJsonProtocol {

  override def route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      register
    }
  }

  private def register = {
    (post & entity(as[RegistrationData])) { registrationData =>
      complete(userFacade.registerUser(registrationData))
    }
  }

}
