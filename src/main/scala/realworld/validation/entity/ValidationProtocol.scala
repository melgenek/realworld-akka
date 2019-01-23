package realworld.validation.entity

import cats.data.NonEmptyList
import io.circe.{Encoder, Json}
import realworld.util.json.CommonProtocol

trait ValidationProtocol extends CommonProtocol {

  implicit val propertyValidationWriter: Encoder[NonEmptyList[PropertyValidation]] =
    wrappedEncoder("errors") { errors =>
      val errorsByProperty = errors.toList.groupBy(_.property).map { case (property, validations) =>
        (property, Json.fromValues(validations.map(_.message).map(Json.fromString)))
      }
      Json.fromFields(errorsByProperty.toSeq)
    }

}
