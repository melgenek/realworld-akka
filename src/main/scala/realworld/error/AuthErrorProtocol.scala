package realworld.error

import io.circe.{Encoder, Json}
import realworld.util.json.CommonProtocol

trait AuthErrorProtocol extends CommonProtocol {

  implicit def authErrorValidationWriter[T <: AuthError]: Encoder[T] = wrappedEncoder("errors") {
    error => Json.obj(error.key -> Json.fromString(error.message))
  }

}
