package realworld.controller

import akka.http.scaladsl.server.Route
import realworld.data.{RegistrationData, UserJsonProtocol}
import realworld.service.UserService
import realworld.util.Controller

class AuthController(userService: UserService) extends Controller with UserJsonProtocol {

  override def route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      register
    }
  }

  private def register = {
    (post & entity(as[RegistrationData])) { registrationData =>
      complete(userService.registerUser(registrationData))
    }
  }

}
