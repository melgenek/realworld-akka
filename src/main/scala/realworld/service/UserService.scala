package realworld.service

import cats._
import cats.implicits._
import realworld.dao.UserDao
import realworld.exception.LoginPasswordAuthException
import realworld.model.User
import realworld.util.ExceptionME

import scala.language.higherKinds

trait UserService[F[_]] {

  def create(user: User): F[User]

  def login(email: String, password: String): F[User]

  def findByEmail(email: String): F[Option[User]]

  def findByUsername(username: String): F[Option[User]]

}

class UserServiceImpl[F[_], DB[_] : Monad](userDao: UserDao[DB],
                                           hashService: HashService,
                                           db: DB ~> F)
                                          (implicit fMonadError: MonadError[F, Throwable]) extends UserService[F] {

  override def create(user: User): F[User] = {
    val userWithHashedPassword: User = user.copy(password = hashService.hashPassword(user.password))
    db {
      userDao.create(userWithHashedPassword).map(_.record)
    }
  }

  override def login(email: String, password: String): F[User] =
    db(userDao.findByEmail(email)).flatMap { userOpt =>
      userOpt.map { user =>
        if (hashService.isPasswordCorrect(password, user.record.password)) ExceptionME[F].pure(user.record)
        else ExceptionME[F].raiseError[User](LoginPasswordAuthException)
      }.getOrElse(ExceptionME[F].raiseError(LoginPasswordAuthException))
    }

  override def findByEmail(email: String): F[Option[User]] =
    db {
      userDao.findByEmail(email).map(_.map(_.record))
    }

  override def findByUsername(username: String): F[Option[User]] =
    db {
      userDao.findByUsername(username).map(_.map(_.record))
    }

}
