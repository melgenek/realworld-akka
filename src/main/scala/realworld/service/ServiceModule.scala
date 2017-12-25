package realworld.service

import com.softwaremill.macwire._
import realworld.config.ActorModule
import realworld.dao.DaoModule

trait ServiceModule extends DaoModule with ActorModule {

  lazy val authService: AuthService = wire[AuthServiceImpl]

  lazy val hashService: HashService = wire[BCryptHashService]

  lazy val userService: UserService = wire[UserServiceImpl]

}
