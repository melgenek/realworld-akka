package realworld.parse

import cats.implicits._
import realworld.validation.entity.EmptyProperty
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.{DefaultJsonProtocol, JsValue, JsonReader}

import scala.util.Try

trait Parser[R] extends DefaultJsonProtocol {

  def parse(json: JsValue): ValidationResult[R]

  protected def field[T](value: JsValue, fieldName: String)(implicit reader: JsonReader[T]): ValidationResult[T] =
    Try(reader.read(value.asJsObject.fields(fieldName)))
      .map(_.validNel)
      .getOrElse(EmptyProperty(fieldName).invalidNel)

  protected def optionField[T](value: JsValue, fieldName: String)(implicit reader: JsonReader[Option[T]]): Option[T] =
    if (!value.asJsObject.fields.contains(fieldName)) None.asInstanceOf[Option[T]]
    else Try(reader.read(value.asJsObject.fields(fieldName)))
      .getOrElse(throw new RuntimeException(s"Could not read $fieldName"))

}
