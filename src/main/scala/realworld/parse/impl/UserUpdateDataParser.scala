package realworld.parse.impl

import cats.implicits._
import realworld.data.UserUpdateData
import realworld.parse.Parser
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.JsValue

class UserUpdateDataParser extends Parser[UserUpdateData] {

  override def parse(json: JsValue): ValidationResult[UserUpdateData] = {
    val form = json.asJsObject.fields("user")
    val email: Option[String] = optionField[String](form, "email")
    val username: Option[String] = optionField[String](form, "username")
    val password: Option[String] = optionField[String](form, "password")
    val image: Option[String] = optionField[String](form, "image")
    val bio: Option[String] = optionField[String](form, "bio")
    UserUpdateData(
      email = email,
      username = username,
      password = password,
      image = image,
      bio = bio
    ).validNel
  }

}
