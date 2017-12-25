package realworld.dao

import realworld.model.User

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait UserDao {

  def create(user: User): Future[User]

  def get(email: String): Future[User]

}

class InMemoryUserDao(implicit executionContext: ExecutionContext) extends UserDao {

  private val map: mutable.Map[String, User] = mutable.Map.empty

  override def create(user: User): Future[User] = {
    map += user.email -> user
    Future {
      user
    }
  }

  override def get(email: String): Future[User] = Future {
    map(email)
  }

}
