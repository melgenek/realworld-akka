package realworld.exception.handler

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import realworld.exception.{AuthException, ExceptionProtocol}

object AuthExceptionHandler extends Directives with ExceptionProtocol {

  val handler = ExceptionHandler {
    case e: AuthException =>
      complete(StatusCodes.UnprocessableEntity -> e)
  }

}
