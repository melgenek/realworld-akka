package realworld.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{JsObject, JsString, RootJsonWriter, _}

trait UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

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
