package realworld.model

case class User(email: String,
                username: String,
                password: String,
                bio: Option[String] = None,
                image: Option[String] = None)

trait UserModel extends SlickModel {

  import profile.api._

  class Users(t: Tag) extends Table[IdRecord[Long, User]](t, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email", O.Unique)

    def username = column[String]("username", O.Unique)

    def password = column[String]("password")

    def bio = column[String]("bio")

    def image = column[String]("image")

    def user = (email, username, password, bio.?, image.?) <> (User.tupled, User.unapply)

    def * = (id, user).<>[IdRecord[Long, User]]((IdRecord[Long, User] _).tupled, IdRecord.unapply)
  }

  val users = TableQuery[Users]

}
