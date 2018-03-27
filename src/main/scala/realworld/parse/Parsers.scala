package realworld.parse

import realworld.data.{LoginData, RegistrationData, UserUpdateData}
import realworld.parse.impl.{LoginDataParser, RegistrationDataParser, UserUpdateDataParser}

trait Parsers {

  implicit val loginDataParser: Parser[LoginData] = new LoginDataParser

  implicit val registrationDataParser: Parser[RegistrationData] = new RegistrationDataParser

  implicit val userUpdateDataParser: Parser[UserUpdateData] = new UserUpdateDataParser

}
