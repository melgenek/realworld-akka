package realworld.exception

import cats.data.NonEmptyList
import realworld.validation.entity.PropertyValidation

case class PropertyException(errors: NonEmptyList[PropertyValidation])
