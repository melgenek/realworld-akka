package realworld.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import realworld.auth.AuthDirectives
import realworld.data.JsonProtocol
import realworld.error.ErrorProtocol
import realworld.facade.ProfileFacade
import realworld.util.akka.Controller

import scala.concurrent.Future

class ProfileController(profileFacade: ProfileFacade[Future],
                        authDirectives: AuthDirectives) extends Controller
  with JsonProtocol
  with ErrorProtocol {

  override def route: Route = pathPrefix("profiles") {
    authDirectives.authenticate { email =>
      path(Segment) { username =>
        getProfile(username, email)
      }
    }
  }

  protected def getProfile(profileUsername: String, email: String): Route =
    get {
      onSuccess(profileFacade.get(profileUsername, email)) {
        case Right(profile) => complete(profile)
        case Left(e) => complete(StatusCodes.NotFound)
      }
    }

}
