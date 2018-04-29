package realworld.validation

import cats.instances.FutureInstances
import com.softwaremill.macwire._
import realworld.config.ActorModule
import realworld.data.{RegistrationData, UserUpdateData}
import realworld.service.ServiceModule
import realworld.util.db.DBIOInstances
import realworld.validation.impl._

import scala.concurrent.Future

trait ValidationModule extends ServiceModule with ActorModule with FutureInstances with DBIOInstances {

  lazy val emailValidator: EmailValidator[Future] = wire[EmailValidator[Future]]

  lazy val usernameValidator: UsernameValidator[Future] = wire[UsernameValidator[Future]]

  lazy val passwordValidator: PasswordValidator = wire[PasswordValidator]

  lazy val registrationDataValidator: Validator[RegistrationData, Future] = wire[RegistrationDataValidator[Future]]

  lazy val userUpdateDataValidator: Validator[UserUpdateData, Future] = wire[UserUpdateDataValidator[Future]]

}
