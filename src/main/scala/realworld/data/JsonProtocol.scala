package realworld.data

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{JsObject, JsString, RootJsonWriter, _}

trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

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

  implicit val profileDataWriter: RootJsonWriter[ProfileData] = (profile: ProfileData) =>
    JsObject(
      "profile" -> JsObject(
        "email" -> JsString(profile.email),
        "bio" -> profile.bio.toJson,
        "image" -> profile.image.toJson,
        "following" -> JsBoolean(profile.following)
      )
    )

}
