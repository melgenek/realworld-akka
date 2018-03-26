package realworld.exception

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import realworld.validation.entity.ValidationProtocol
import spray.json.{JsObject, JsString, JsValue, RootJsonWriter}

trait ExceptionProtocol extends ValidationProtocol with SprayJsonSupport {

  implicit def authExceptionValidationWriter[T <: AuthError]: RootJsonWriter[T] = new RootJsonWriter[T] {
    override def write(e: T): JsValue = JsObject(
      "errors" -> JsObject(
        e.key -> JsString(e.message)
      )
    )
  }

}
