package realworld.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import realworld.auth.AuthDirectives
import realworld.data.{DataJsonProtocol, LoginData, RegistrationData, UserUpdateData}
import realworld.error.{AuthErrorProtocol, PropertyError}
import realworld.facade.UserFacade
import realworld.util.akka.Controller
import realworld.validation.entity.ValidationProtocol

import scala.concurrent.Future

class UserController(userFacade: UserFacade[Future], authDirectives: AuthDirectives)
  extends Controller
    with DataJsonProtocol
    with AuthErrorProtocol
    with ValidationProtocol {

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
    (post & entity(as[RegistrationData])) { registrationData =>
      onSuccess(userFacade.register(registrationData)) {
        case Right(userData) => complete(userData)
        case Left(PropertyError(errors)) => complete(StatusCodes.UnprocessableEntity -> errors)
      }
    }
  }

  protected def login: Route = {
    (post & entity(as[LoginData])) { loginData =>
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
    (put & entity(as[UserUpdateData])) { updateData =>
      authDirectives.authenticate { email =>
        onSuccess(userFacade.updateUser(email, updateData)) {
          case Right(userData) => complete(userData)
          case Left(PropertyError(errors)) => complete(StatusCodes.UnprocessableEntity -> errors)
        }
      }
    }

}
