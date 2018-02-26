package realworld.service

import org.scalatest.{FlatSpec, Matchers}

class HashServiceSpec extends FlatSpec with Matchers {

  "hashPassword" should "hash and check passwords" in {
    val service = new BCryptHashService
    val password = "some_password"

    val hash: String = service.hashPassword(password)

    service.isPasswordCorrect(password, hash) should be(true)
    service.isPasswordCorrect("another_password", hash) should be(false)
  }

}
