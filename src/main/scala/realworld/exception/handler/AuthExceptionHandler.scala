package realworld.exception.handler

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import realworld.exception.{ExceptionProtocol, LoginPasswordAuthException, TokenException}

object AuthExceptionHandler extends Directives with ExceptionProtocol {

  val handler = ExceptionHandler {
    case e@LoginPasswordAuthException =>
      complete(StatusCodes.UnprocessableEntity -> e)
    case e@TokenException =>
      complete(StatusCodes.Forbidden -> e)
  }

}
