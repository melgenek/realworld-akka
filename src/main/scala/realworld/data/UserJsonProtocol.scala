package realworld.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat, RootJsonReader, RootJsonWriter}
import spray.json._

trait UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val registrationDataFormat: RootJsonReader[RegistrationData] = (json: JsValue) => {
    val form = json.asJsObject.fields("user")
    val username = fromField[String](form, "username")
    val email = fromField[String](form, "email")
    val password = fromField[String](form, "password")
    RegistrationData(username, email, password)
  }

  implicit val userDataFormat: RootJsonWriter[UserData] = (data: UserData) =>
    JsObject(
      "user" -> JsObject(
        "token" -> JsString(data.token),
        "email" -> JsString(data.email),
        "username" -> JsString(data.username),
        "bio" -> data.bio.toJson,
        "image" -> data.image.toJson
      )
    )

  //  override def read(json: JsValue): UserData = {
  //    val form = json.asJsObject("user")
  //    val username = fromField[String](form, "username")
  //    val email = fromField[String](form, "email")
  //    val password = fromField[String](form, "password")
  //    UserData(username, email, password)
  //  }

}
