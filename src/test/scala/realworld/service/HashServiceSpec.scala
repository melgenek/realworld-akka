package realworld.service

import org.scalatest.{FlatSpec, Matchers}

class HashServiceSpec extends FlatSpec with Matchers {

  "hashPassword" should "hash and check passwords" in {
    val service = new BCryptHashService
    val password = "some_password"

    val hash: String = service.hashPassword(password)

    service.checkPassword(password, hash) should be(true)
    service.checkPassword("another_password", hash) should be(false)
  }

}
