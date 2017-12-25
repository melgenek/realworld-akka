package realworld.service

import io.scalaland.chimney.dsl._
import realworld.dao.UserDao
import realworld.data.{RegistrationData, UserData}
import realworld.model.User

import scala.concurrent.{ExecutionContext, Future}

trait UserService {

  def registerUser(registrationData: RegistrationData): Future[UserData]

}

class UserServiceImpl(userDao: UserDao,
                      hashService: HashService,
                      authService: AuthService)
                     (implicit executionContext: ExecutionContext) extends UserService {

  override def registerUser(registrationData: RegistrationData): Future[UserData] = {
    val user = User(
      email = registrationData.email,
      username = registrationData.username,
      passwordHash = hashService.hashPassword(registrationData.password)
    )
    userDao.create(user).map(_.into[UserData]
      .withFieldComputed(_.token, u => authService.createTokenByEmail(u.email))
      .transform
    )
  }

}
