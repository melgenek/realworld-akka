package realworld.data

case class UserData(email: String,
                    username: String,
                    token: String,
                    bio: Option[String] = None,
                    image: Option[String] = None)
