package realworld.service

import cats._
import cats.implicits._
import realworld.dao.UserDao
import realworld.model.User

import scala.language.higherKinds

trait UserService[F[_]] {

  def create(user: User): F[User]

  def findByEmail(email: String): F[Option[User]]

  def getByEmail(email: String): F[User]

  def findByUsername(username: String): F[Option[User]]

  def update(oldEmail: String, user: User): F[User]

}

class UserServiceImpl[F[_] : Monad, DB[_] : Monad](userDao: UserDao[DB],
                                                   db: DB ~> F) extends UserService[F] {

  override def create(user: User): F[User] =
    db {
      userDao.create(user).map(_.record)
    }

  override def findByEmail(email: String): F[Option[User]] =
    db {
      userDao.findByEmail(email).map(_.map(_.record))
    }

  override def getByEmail(email: String): F[User] =
    db {
      userDao.getByEmail(email).map(_.record)
    }

  override def findByUsername(username: String): F[Option[User]] =
    db {
      userDao.findByUsername(username).map(_.map(_.record))
    }

  override def update(oldEmail: String, user: User): F[User] =
    db {
      for {
        oldUser <- userDao.getByEmail(oldEmail)
        newUser = oldUser.copy(record = user)
        _ <- userDao.update(newUser)
      } yield newUser.record
    }

}
