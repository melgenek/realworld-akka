package realworld.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonReader, RootJsonWriter, _}

trait UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val registrationDataReader: RootJsonReader[RegistrationData] = (json: JsValue) => {
    val form = json.asJsObject.fields("user")
    val email: Option[String] = fromField[Option[String]](form, "email")
    val username: Option[String] = fromField[Option[String]](form, "username")
    val password: Option[String] = fromField[Option[String]](form, "password")
    RegistrationData(email, username, password)
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
