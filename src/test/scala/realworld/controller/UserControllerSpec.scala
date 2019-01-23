package realworld.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.softwaremill.macwire._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._
import io.circe.parser._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.auth.{AuthDirectives, EmailAuthenticator}
import realworld.data.UserData
import realworld.facade.UserFacade
import realworld.util.{SpecImplicits, TestData}

import scala.concurrent.Future

class UserControllerSpec
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with MockitoSugar
    with FailFastCirceSupport
    with SpecImplicits {

  import TestData._

  "register" should "return created user" in new Wiring {
    val registrationData: Json =
      parse(
        s"""{
           |  "user":{
           |    "username": "$UserName",
           |    "email": "$Email",
           |    "password": "$Password"
           |  }
           |}""".stripMargin).getOrElse(Json.Null)

    Post("/users", registrationData) ~> controller.route ~> check {
      status shouldEqual StatusCodes.OK
      val responseJson = responseAs[JsonObject]
      val user: Map[String, Json] = responseJson("user").flatMap(_.asObject).get.toMap
      user("email") should equal(Json.fromString(Email))
      user("token") should equal(Json.fromString(Token))
      user("username") should equal(Json.fromString(UserName))
      user("bio") should equal(Json.Null)
      user("image") should equal(Json.Null)
    }
  }

  "login" should "return authenticated user" in new Wiring {
    val loginData: Json =
      parse(
        s"""{
           |  "user":{
           |    "email": "$Email",
           |    "password": "$Password"
           |  }
           |}""".stripMargin).getOrElse(Json.Null)

    Post("/users/login", loginData) ~> controller.route ~> check {
      status shouldEqual StatusCodes.OK
      val responseJson = responseAs[JsonObject]
      val user: Map[String, Json] = responseJson("user").flatMap(_.asObject).get.toMap
      user("email") should equal(Json.fromString(Email))
      user("token") should equal(Json.fromString(Token))
      user("username") should equal(Json.fromString(UserName))
      user("bio") should equal(Json.Null)
      user("image") should equal(Json.Null)
    }
  }

  private trait Wiring {
    val userData = UserData(Email, UserName, Token, None, None)

    val userFacade: UserFacade[Future] = mock[UserFacade[Future]]
    when(userFacade.register(any())).thenReturnAsync(Right(userData))
    when(userFacade.login(any())).thenReturnAsync(Right(userData))

    val emailAuthenticator: EmailAuthenticator = mock[EmailAuthenticator]
    val authDirectives: AuthDirectives = new AuthDirectives(emailAuthenticator)

    val controller: UserController = wire[UserController]
  }

}
