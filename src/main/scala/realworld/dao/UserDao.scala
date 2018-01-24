package realworld.dao

import realworld.model.{User, UserModel}
import slick.dbio._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait UserDao[F[_]] {

  def create(user: User): F[User]

  def get(email: String): F[User]

}

class UserDaoImpl(val profile: JdbcProfile)(implicit ec: ExecutionContext) extends UserDao[DBIO] with UserModel {

  import profile.api._

  override def create(user: User): DBIO[User] =
    (users += user).map(_ => user)

  override def get(email: String): DBIO[User] =
    users.filter(_.email === email).result.head

}
