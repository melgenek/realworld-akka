package realworld.auth

import com.softwaremill.macwire._
import realworld.service.ServiceModule

trait AuthModule extends ServiceModule {

  lazy val emailAuthenticator: EmailAuthenticator = wire[EmailAuthenticator]

  lazy val authDirectives: AuthDirectives = wire[AuthDirectives]

}
