package realworld.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.softwaremill.macwire._
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import realworld.data.UserData
import realworld.facade.UserFacade
import realworld.util.{SpecImplicits, TestData}
import spray.json._

import scala.concurrent.Future

class UserControllerSpec
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with MockitoSugar
    with DefaultJsonProtocol
    with SprayJsonSupport
    with SpecImplicits {

  import TestData._

  "register" should "return created user" in new Wiring {
    val registrationData: JsValue =
      s"""{
         |  "user":{
         |    "username": "$UserName",
         |    "email": "$Email",
         |    "password": "$Password"
         |  }
         |}""".stripMargin.parseJson

    Post("/users", registrationData) ~> controller.route ~> check {
      status shouldEqual StatusCodes.OK
      val user: Map[String, JsValue] = responseAs[JsObject].fields("user").asJsObject.fields
      user("email") should equal(JsString(Email))
      user("token") should equal(JsString(Token))
      user("username") should equal(JsString(UserName))
      user("bio") should equal(JsNull)
      user("image") should equal(JsNull)
    }
  }

  "login" should "return authenticated user" in new Wiring {
    val loginData: JsValue =
      s"""{
         |  "user":{
         |    "email": "$Email",
         |    "password": "$Password"
         |  }
         |}""".stripMargin.parseJson

    Post("/users/login", loginData) ~> controller.route ~> check {
      status shouldEqual StatusCodes.OK
      val user: Map[String, JsValue] = responseAs[JsObject].fields("user").asJsObject.fields
      user("email") should equal(JsString(Email))
      user("token") should equal(JsString(Token))
      user("username") should equal(JsString(UserName))
      user("bio") should equal(JsNull)
      user("image") should equal(JsNull)
    }
  }

  private trait Wiring {
    val userData = UserData(Email, UserName, Token, None, None)

    val userFacade: UserFacade[Future] = mock[UserFacade[Future]]
    when(userFacade.register(any())).thenReturnAsync(userData)
    when(userFacade.login(any())).thenReturnAsync(userData)

    val controller: UserController = wire[UserController]
  }

}