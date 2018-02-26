package realworld.rejection.handler

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.RejectionHandler
import cats.data.NonEmptyList
import realworld.exception.handler.ValidationExceptionHandler.complete
import realworld.rejection.{MissingJsonPropertyRejection, PropertyValidationRejection}
import realworld.validation.entity.{EmptyProperty, PropertyValidation, ValidationProtocol}

object ValidationRejectionHandler extends ValidationProtocol {

  val handler: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case MissingJsonPropertyRejection(prop) =>
        val errors: NonEmptyList[PropertyValidation] = NonEmptyList.one(EmptyProperty(prop))
        complete(StatusCodes.UnprocessableEntity -> errors)
      case PropertyValidationRejection(errors) =>
        complete(StatusCodes.UnprocessableEntity -> errors)
    }.result()

}
