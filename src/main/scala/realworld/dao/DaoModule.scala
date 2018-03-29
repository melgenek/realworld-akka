package realworld.dao

import com.softwaremill.macwire._
import realworld.config.{ActorModule, DbModule}
import slick.dbio.DBIO

trait DaoModule extends DbModule with ActorModule {

  lazy val userDao: UserDao[DBIO] = wire[UserDaoImpl]

  lazy val relationDao: RelationDao[DBIO] = wire[RelationDaoImpl]

}
