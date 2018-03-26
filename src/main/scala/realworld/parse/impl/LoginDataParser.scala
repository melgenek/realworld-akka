package realworld.parse.impl

import cats.implicits._
import realworld.data.LoginData
import realworld.parse.Parser
import realworld.validation.entity.PropertyValidation.ValidationResult
import spray.json.JsValue

class LoginDataParser extends Parser[LoginData] {

  override def parse(json: JsValue): ValidationResult[LoginData] = {
    val form = json.asJsObject.fields("user")
    val emailV: ValidationResult[String] = field[String](form, "email")
    val passwordV: ValidationResult[String] = field[String](form, "password")
    (emailV, passwordV).mapN { (email, password) =>
      LoginData(email, password)
    }
  }

}
