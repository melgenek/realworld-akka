package realworld.validation

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import realworld.exception.PropertyException
import realworld.validation.entity.ValidationProtocol

object ValidationExceptionHandler extends Directives with ValidationProtocol {

  val handler = ExceptionHandler {
    case PropertyException(errors) => complete(StatusCodes.UnprocessableEntity -> errors)
  }

}
