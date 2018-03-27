package realworld.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import realworld.auth.AuthDirectives
import realworld.data.{LoginData, RegistrationData, UserJsonProtocol, UserUpdateData}
import realworld.exception.{ExceptionProtocol, PropertyError}
import realworld.facade.UserFacade
import realworld.parse.{ParseDirectives, Parsers}
import realworld.util.akka.Controller

import scala.concurrent.Future

class UserController(userFacade: UserFacade[Future],
                     authDirectives: AuthDirectives)
  extends Controller
    with ParseDirectives
    with Parsers
    with UserJsonProtocol
    with ExceptionProtocol {

  override def route: Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      register
    } ~ path("login") {
      login
    }
  } ~ path("user") {
    info ~ update
  }

  protected def register: Route = {
    (post & parse[RegistrationData]) { registrationData =>
      onSuccess(userFacade.register(registrationData)) {
        case Right(userData) => complete(userData)
        case Left(PropertyError(errors)) => complete(StatusCodes.UnprocessableEntity -> errors)
      }
    }
  }

  protected def login: Route = {
    (post & parse[LoginData]) { loginData =>
      onSuccess(userFacade.login(loginData)) {
        case Right(userData) => complete(userData)
        case Left(e) => complete(StatusCodes.UnprocessableEntity -> e)
      }
    }
  }

  protected def info: Route =
    get {
      authDirectives.authenticate { email =>
        onSuccess(userFacade.getByEmail(email)) {
          case Right(userData) => complete(userData)
          case Left(e) => complete(StatusCodes.Forbidden -> e)
        }
      }
    }

  protected def update: Route =
    (put & parse[UserUpdateData]) { updateData =>
      authDirectives.authenticate { email =>
        onSuccess(userFacade.updateUser(email, updateData)) {
          case Right(userData) => complete(userData)
          case Left(PropertyError(errors)) => complete(StatusCodes.UnprocessableEntity -> errors)
        }
      }
    }

}
