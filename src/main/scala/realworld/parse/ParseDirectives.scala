package realworld.parse

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, Directives, MalformedRequestContentRejection, RequestEntityExpectedRejection, UnsupportedRequestContentTypeRejection}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import cats.data.Validated.{Invalid, Valid}
import realworld.validation.entity.ValidationProtocol
import spray.json._

import scala.util.{Failure, Success}

trait ParseDirectives extends Directives with SprayJsonSupport with ValidationProtocol {

  def parse[T](implicit parser: Parser[T]): Directive1[T] =
    extractRequestContext.flatMap[Tuple1[T]] { ctx =>
      import ctx.{executionContext, materializer}
      val um: FromRequestUnmarshaller[JsValue] = implicitly[FromRequestUnmarshaller[JsValue]]
      onComplete(um(ctx.request)) flatMap {
        case Success(json) =>
          parser.parse(json) match {
            case Valid(value) => provide(value)
            case Invalid(errors) => complete(StatusCodes.UnprocessableEntity -> errors)
          }
        case Failure(x) ⇒ reject(MalformedRequestContentRejection(x.getMessage, x))
      }
    } & cancelRejections(RequestEntityExpectedRejection.getClass, classOf[UnsupportedRequestContentTypeRejection])

}
