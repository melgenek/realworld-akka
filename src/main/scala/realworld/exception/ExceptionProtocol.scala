package realworld.exception

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonWriter}

trait ExceptionProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit def propertyValidationWriter[T <: AuthException]: RootJsonWriter[T] = new RootJsonWriter[T] {
    override def write(e: T): JsValue = JsObject(
      "errors" -> JsObject(
        e.key -> JsString(e.message)
      )
    )
  }

}
