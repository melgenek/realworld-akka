package realworld.controller

import akka.http.scaladsl.server.Route
import realworld.data.{LoginData, RegistrationData, UserJsonProtocol}
import realworld.facade.UserFacade
import realworld.util.akka.Controller

import scala.concurrent.Future

class UserController(userFacade: UserFacade[Future]) extends Controller with UserJsonProtocol {

  override def route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      register
    } ~ path("login") {
      login
    }
  }

  protected def register: Route = {
    (post & entity(as[RegistrationData])) { registrationData =>
      complete(userFacade.register(registrationData))
    }
  }

  protected def login: Route = {
    (post & entity(as[LoginData])) { loginData =>
      complete(userFacade.login(loginData))
    }
  }

}
