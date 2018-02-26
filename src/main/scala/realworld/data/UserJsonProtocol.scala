package realworld.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import cats.implicits._
import realworld.util.json.CommonJsonProtocol
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.{JsObject, JsString, JsValue, RootJsonReader, RootJsonWriter, _}

trait UserJsonProtocol extends CommonJsonProtocol with SprayJsonSupport {

  implicit val registrationDataReader: RootJsonReader[RegistrationData] = (json: JsValue) => {
    val form = json.asJsObject.fields("user")
    val emailV: ValidationResult[String] = validatedFromField[String](form, "email")
    val usernameV: ValidationResult[String] = validatedFromField[String](form, "username")
    val passwordV: ValidationResult[String] = validatedFromField[String](form, "password")
    getOrReject[RegistrationData](
      (emailV, usernameV, passwordV).mapN { (email, username, password) =>
        RegistrationData(email, username, password)
      }
    )
  }

  implicit val loginDataReader: RootJsonReader[LoginData] = (json: JsValue) => {
    val form = json.asJsObject.fields("user")
    val emailV: ValidationResult[String] = validatedFromField[String](form, "email")
    val passwordV: ValidationResult[String] = validatedFromField[String](form, "password")
    getOrReject[LoginData](
      (emailV, passwordV).mapN { (email, password) =>
        LoginData(email, password)
      }
    )
  }

  implicit val userDataWriter: RootJsonWriter[UserData] = (data: UserData) =>
    JsObject(
      "user" -> JsObject(
        "token" -> JsString(data.token),
        "email" -> JsString(data.email),
        "username" -> JsString(data.username),
        "bio" -> data.bio.toJson,
        "image" -> data.image.toJson
      )
    )

}
