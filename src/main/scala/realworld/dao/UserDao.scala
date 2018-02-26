package realworld.dao

import realworld.model.{User, UserModel}
import slick.dbio._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait UserDao[F[_]] {

  def create(user: User): F[User]

  def findByEmail(email: String): F[Option[User]]

  def findByUsername(username: String): F[Option[User]]

}

class UserDaoImpl(val profile: JdbcProfile)(implicit ec: ExecutionContext) extends UserDao[DBIO] with UserModel {

  import profile.api._

  override def create(user: User): DBIO[User] =
    (users += user).map(_ => user)

  override def findByEmail(email: String): DBIO[Option[User]] =
    users.filter(_.email === email.bind).result.headOption

  override def findByUsername(username: String): DBIO[Option[User]] =
    users.filter(_.username === username.bind).result.headOption

}
