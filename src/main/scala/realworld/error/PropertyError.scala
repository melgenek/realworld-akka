package realworld.error

import cats.data.NonEmptyList
import realworld.validation.entity.PropertyValidation

case class PropertyError(errors: NonEmptyList[PropertyValidation])
