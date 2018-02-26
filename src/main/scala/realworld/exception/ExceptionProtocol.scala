package realworld.exception

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsObject, JsString, RootJsonWriter}

trait ExceptionProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val propertyValidationWriter: RootJsonWriter[AuthException] =
    (e: AuthException) => {
      JsObject(
        "errors" -> JsObject(
          e.key -> JsString(e.message)
        )
      )
    }

}
