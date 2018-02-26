package realworld.service

import cats.Id
import com.softwaremill.macwire._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.model.User
import realworld.util.{IdInstances, TestData}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "create" should "hash password and create user using dao" in new Wiring {
    service.create(user)

    val userCaptor: ArgumentCaptor[User] = ArgumentCaptor.forClass(classOf[User])
    verify(userDao).create(userCaptor.capture())

    val result: User = userCaptor.getValue
    result.email should equal(Email)
    result.username should equal(UserName)
    result.password should equal(PasswordHash)
  }

  it should "return created user" in new Wiring {
    val result: User = service.create(user)

    result should equal(createdUser)
  }

  private trait Wiring extends IdInstances {
    val userDao: UserDao[Id] = mock[UserDao[Id]]
    val createdUser: User = User(Email, UserName, PasswordHash, Some(Bio), Some(Image))
    when(userDao.create(any())).thenReturn(createdUser)

    val hashService: HashService = mock[HashService]
    when(hashService.hashPassword(Password)).thenReturn(PasswordHash)

    val service: UserService[Id] = wire[UserServiceImpl[Id, Id]]
  }

}
