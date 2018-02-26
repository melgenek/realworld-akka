package realworld.util.json

import akka.http.scaladsl.server.RejectionError
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import realworld.rejection.{MissingJsonPropertyRejection, PropertyValidationRejection}
import realworld.validation.entity.EmptyProperty
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.{DefaultJsonProtocol, DeserializationException, JsObject, JsValue, JsonReader, deserializationError}

import scala.util.{Failure, Success, Try}

trait CommonJsonProtocol extends DefaultJsonProtocol {


  protected def validatedFromField[T](value: JsValue, fieldName: String)
                                     (implicit reader: JsonReader[T]): ValidationResult[T] = {
    Try(fromField[T](value, fieldName)) match {
      case Success(res) => res.validNel
      case Failure(RejectionError(MissingJsonPropertyRejection(prop))) => EmptyProperty(prop).invalidNel
      case Failure(e) => throw e
    }
  }

  protected def getOrReject[T](res: ValidationResult[T]): T = {
    res match {
      case Valid(t) => t
      case Invalid(errors) => throw RejectionError(PropertyValidationRejection(errors))
    }
  }

  override protected def fromField[T](value: JsValue, fieldName: String)
                                     (implicit reader: JsonReader[T]): T = value match {
    case x: JsObject if reader.isInstanceOf[OptionFormat[_]] & !x.fields.contains(fieldName) =>
      None.asInstanceOf[T]
    case x: JsObject =>
      try reader.read(x.fields(fieldName))
      catch {
        case e: NoSuchElementException =>
          throw RejectionError(MissingJsonPropertyRejection(fieldName))
        case DeserializationException(msg, cause, fieldNames) =>
          deserializationError(msg, cause, fieldName :: fieldNames)
      }
    case _ => deserializationError("Object expected in field '" + fieldName + "'", fieldNames = fieldName :: Nil)
  }

}
