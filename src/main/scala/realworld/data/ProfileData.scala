package realworld.data

case class ProfileData(username: String,
                       following: Boolean = false,
                       bio: Option[String] = None,
                       image: Option[String] = None)
