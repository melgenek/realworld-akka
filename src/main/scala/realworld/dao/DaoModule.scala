package realworld.dao

import com.softwaremill.macwire._
import realworld.config.ActorModule

trait DaoModule extends ActorModule {

  lazy val userDao: UserDao = wire[InMemoryUserDao]

}
