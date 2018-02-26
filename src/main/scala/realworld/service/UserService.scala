package realworld.service

import cats._
import realworld.dao.UserDao
import realworld.model.User

import scala.language.higherKinds

trait UserService[F[_]] {

  def create(user: User): F[User]

}

class UserServiceImpl[F[_] : Monad, DB[_] : Monad](userDao: UserDao[DB],
                                                   hashService: HashService,
                                                   db: DB ~> F) extends UserService[F] {

  override def create(user: User): F[User] = {
    val userWithHashedPassword: User = user.copy(password = hashService.hashPassword(user.password))
    db {
      userDao.create(userWithHashedPassword)
    }
  }

}
