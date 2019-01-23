package realworld.error

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, MalformedRequestContentRejection, RejectionHandler}
import cats.data.NonEmptyList
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport.DecodingFailures
import io.circe.CursorOp.DownField
import io.circe.DecodingFailure
import realworld.validation.entity.{EmptyProperty, PropertyValidation, ValidationProtocol}

object ErrorRejectionHandler extends Directives with ValidationProtocol with ErrorAccumulatingCirceSupport {

  implicit val handler: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case MalformedRequestContentRejection(_, DecodingFailures(failures)) =>
        val fieldErrors = failures.toList.flatMap(fieldErrorsFromFailure)
        if (fieldErrors.nonEmpty)
          complete(StatusCodes.UnprocessableEntity -> NonEmptyList.fromListUnsafe[PropertyValidation](fieldErrors))
        else
          complete(StatusCodes.UnprocessableEntity)
    }
    .result()

  private def fieldErrorsFromFailure(decodingFailure: DecodingFailure): Seq[EmptyProperty] =
    decodingFailure.history.collect { case DownField(field) => EmptyProperty(field) }

}
