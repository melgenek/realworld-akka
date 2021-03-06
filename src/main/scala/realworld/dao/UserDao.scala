package realworld.dao

import realworld.model.IdRecord.IdUser
import realworld.model.{IdRecord, User, UserModel}
import slick.dbio._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

trait UserDao[F[_]] {

  def create(user: User): F[IdUser]

  def findByEmail(email: String): F[Option[IdUser]]

  def getByEmail(email: String): F[IdUser]

  def findByUsername(username: String): F[Option[IdUser]]

  def update(user: IdUser): F[Unit]

}

class UserDaoImpl(val profile: JdbcProfile)(implicit ec: ExecutionContext) extends UserDao[DBIO] with UserModel {

  import profile.api._

  override def create(user: User): DBIO[IdUser] =
    users returning users.map(_.id) into ((record, id) => record.copy(id = id)) += IdRecord(0, user)

  override def findByEmail(email: String): DBIO[Option[IdUser]] =
    users.filter(_.email === email.bind).result.headOption

  override def getByEmail(email: String): DBIO[IdUser] =
    users.filter(_.email === email.bind).result.head

  override def findByUsername(username: String): DBIO[Option[IdUser]] =
    users.filter(_.username === username.bind).result.headOption

  def update(user: IdUser): DBIO[Unit] =
    users.filter(_.id === user.id).update(user).map(_ => ())

}
