package realworld.rejection

import akka.http.scaladsl.server.Rejection
import cats.data.NonEmptyList
import realworld.validation.entity.PropertyValidation

case class PropertyValidationRejection(errors: NonEmptyList[PropertyValidation]) extends Rejection
