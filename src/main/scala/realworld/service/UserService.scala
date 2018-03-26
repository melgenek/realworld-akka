package realworld.service

import cats._
import cats.implicits._
import realworld.dao.UserDao
import realworld.exception.LoginPasswordAuthError
import realworld.model.User

import scala.language.higherKinds

trait UserService[F[_]] {

  def create(user: User): F[User]

  def login(email: String, password: String): F[Either[LoginPasswordAuthError, User]]

  def findByEmail(email: String): F[Option[User]]

  def findByUsername(username: String): F[Option[User]]

}

class UserServiceImpl[F[_] : Monad, DB[_] : Monad](userDao: UserDao[DB],
                                                   hashService: HashService,
                                                   db: DB ~> F) extends UserService[F] {

  override def create(user: User): F[User] = {
    val userWithHashedPassword: User = user.copy(password = hashService.hashPassword(user.password))
    db {
      userDao.create(userWithHashedPassword).map(_.record)
    }
  }

  override def login(email: String, password: String): F[Either[LoginPasswordAuthError, User]] =
    db(userDao.findByEmail(email)).flatMap { userOpt =>
      val res: Either[LoginPasswordAuthError, User] = userOpt.map { user =>
        if (hashService.isPasswordCorrect(password, user.record.password)) user.record.asRight[LoginPasswordAuthError]
        else LoginPasswordAuthError().asLeft[User]
      }.getOrElse(LoginPasswordAuthError().asLeft[User])
      Monad[F].pure(res)
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
