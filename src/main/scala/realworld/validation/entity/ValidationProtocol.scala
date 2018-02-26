package realworld.validation.entity

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import cats.data.NonEmptyList
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, RootJsonWriter}

trait ValidationProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val propertyValidationWriter: RootJsonWriter[NonEmptyList[PropertyValidation]] =
    (errors: NonEmptyList[PropertyValidation]) => {
      val errorsByProperty: Map[String, JsArray] = errors.toList.groupBy(_.property).map { case (property, validations) =>
        (property, JsArray(validations.map(v => JsString(v.message)).toVector))
      }
      JsObject(
        "errors" -> JsObject(
          errorsByProperty
        )
      )
    }

}
