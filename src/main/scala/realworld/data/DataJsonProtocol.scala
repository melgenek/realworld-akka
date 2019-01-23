package realworld.data

import io.circe._
import io.circe.generic.semiauto._
import realworld.util.json.CommonProtocol

trait DataJsonProtocol extends CommonProtocol{

  implicit val userDataEncoder: Encoder[UserData] = wrappedEncoder("user")(deriveEncoder[UserData])
  implicit val profileDataEncoder: Encoder[ProfileData] = wrappedEncoder("profile")(deriveEncoder[ProfileData])

  implicit val loginDataDecoder: Decoder[LoginData] = wrappedDecoder("user")(deriveDecoder[LoginData])
  implicit val userUpdateDataDecoder: Decoder[UserUpdateData] = wrappedDecoder("user")(deriveDecoder[UserUpdateData])
  implicit val registrationDataDecoder: Decoder[RegistrationData] = wrappedDecoder("user")(deriveDecoder[RegistrationData])

}
