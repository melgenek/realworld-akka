package realworld.service

import cats.Id
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.dao.UserDao
import realworld.model.{IdRecord, User}
import realworld.util.{IdInstances, TestData}

class UserServiceSpec
  extends FlatSpec
    with Matchers
    with MockitoSugar {

  import TestData._

  "create" should "return created user" in new Wiring {
    val result: User = service.create(user)

    result should equal(createdUser)
  }

  private trait Wiring extends IdInstances {
    val userDao: UserDao[Id] = mock[UserDao[Id]]
    val createdUser: User = User(Email, UserName, PasswordHash, Some(Bio), Some(Image))
    val userRecord = IdRecord(0L, createdUser)
    when(userDao.create(any())).thenReturn(userRecord)
    when(userDao.findByEmail(any())).thenReturn(Some(userRecord))

    val service: UserService[Id] = wire[UserServiceImpl[Id, Id]]
  }

}
