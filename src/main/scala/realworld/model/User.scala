package realworld.model

case class User(email: String,
                username: String,
                passwordHash: String,
                bio: Option[String] = None,
                image: Option[String] = None)
