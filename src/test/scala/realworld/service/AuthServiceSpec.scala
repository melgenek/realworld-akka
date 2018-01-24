package realworld.service

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{FlatSpec, Matchers}

class AuthServiceSpec extends FlatSpec with Matchers {

  "createTokenByEmail" should "hash and check passwords" in {
    val config: Config = ConfigFactory.load()
    val service = new AuthServiceImpl(config)
    val email = "email"

    val token: String = service.createTokenByEmail(email)

    service.validateAndGetEmail(token) should equal(Right(email))
  }

}
