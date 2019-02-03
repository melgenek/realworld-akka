package realworld.service

import cats._
import cats.data.EitherT
import cats.implicits._
import realworld.dao.UserDao
import realworld.error.LoginPasswordAuthError
import realworld.model.User

import scala.language.higherKinds

trait UserService[F[_]] {

  def login(email: String, password: String): EitherT[F, LoginPasswordAuthError, User]

  def create(user: User): F[User]

  def findByEmail(email: String): F[Option[User]]

  def getByEmail(email: String): F[User]

  def findByUsername(username: String): F[Option[User]]

  def update(oldEmail: String, user: User): F[User]

}

class UserServiceImpl[F[_] : Monad, DB[_] : Monad](userDao: UserDao[DB],
                                                   hashService: HashService,
                                                   db: DB ~> F) extends UserService[F] {

  override def login(email: String, password: String): EitherT[F, LoginPasswordAuthError, User] =
    EitherT.fromOptionF(findByEmail(email), LoginPasswordAuthError()).subflatMap { user =>
      if (hashService.isPasswordCorrect(password, user.password)) user.asRight
      else LoginPasswordAuthError().asLeft
    }

  override def create(user: User): F[User] = {
    val userWithHashedPassword: User = user.copy(password = hashService.hashPassword(user.password))
    db(userDao.create(userWithHashedPassword).map(_.record))
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
        userWithHashedPassword = if (oldUser.record.password == user.password) user
        else user.copy(password = hashService.hashPassword(user.password))
        newUser = oldUser.copy(record = userWithHashedPassword)
        _ <- userDao.update(newUser)
      } yield newUser.record
    }

}
