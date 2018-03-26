package realworld.parse.impl

import cats.implicits._
import realworld.data.RegistrationData
import realworld.parse.Parser
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.JsValue

class RegistrationDataParser extends Parser[RegistrationData] {

  override def parse(json: JsValue): ValidationResult[RegistrationData] = {
    val form = json.asJsObject.fields("user")
    val emailV: ValidationResult[String] = field[String](form, "email")
    val usernameV: ValidationResult[String] = field[String](form, "username")
    val passwordV: ValidationResult[String] = field[String](form, "password")
    (emailV, usernameV, passwordV).mapN { (email, username, password) =>
      RegistrationData(email, username, password)
    }
  }

}
