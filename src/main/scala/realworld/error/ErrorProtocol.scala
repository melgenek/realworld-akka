package realworld.error

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import realworld.validation.entity.ValidationProtocol
import spray.json.{JsObject, JsString, JsValue, RootJsonWriter}

trait ErrorProtocol extends ValidationProtocol with SprayJsonSupport {

  implicit def authErrorValidationWriter[T <: AuthError]: RootJsonWriter[T] = new RootJsonWriter[T] {
    override def write(e: T): JsValue = JsObject(
      "errors" -> JsObject(
        e.key -> JsString(e.message)
      )
    )
  }

}
