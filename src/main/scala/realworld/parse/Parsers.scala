package realworld.parse

import realworld.data.{LoginData, RegistrationData}
import realworld.parse.impl.{LoginDataParser, RegistrationDataParser}

trait Parsers {

  implicit val loginDataParser: Parser[LoginData] = new LoginDataParser

  implicit val registrationDataParser: Parser[RegistrationData] = new RegistrationDataParser

}
