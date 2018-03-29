package realworld.data

case class ProfileData(email: String,
                       following: Boolean = false,
                       bio: Option[String] = None,
                       image: Option[String] = None)
