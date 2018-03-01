package realworld.controller

import akka.http.scaladsl.server.Route
import realworld.auth.{AuthDirectives, EmailAuthenticator}
import realworld.data.{LoginData, RegistrationData, UserJsonProtocol}
import realworld.facade.UserFacade
import realworld.util.akka.Controller

import scala.concurrent.Future

class UserController(userFacade: UserFacade[Future],
                     val emailAuthenticator: EmailAuthenticator) extends Controller with AuthDirectives with UserJsonProtocol {

  override def route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      register
    } ~ path("login") {
      login
    }
  } ~ path("user") {
    info
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

  protected def info: Route =
    get {
      authenticate { email =>
        complete(userFacade.getByEmail(email))
      }
    }

}
