package realworld.data

case class UserUpdateData(email: Option[String],
                          username: Option[String],
                          password: Option[String],
                          bio: Option[String],
                          image: Option[String])
