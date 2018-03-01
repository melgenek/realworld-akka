package realworld.auth

import akka.http.scaladsl.model.headers.HttpCredentials
import realworld.service.TokenService

class EmailAuthenticator(tokenService: TokenService) extends (Option[HttpCredentials] => Option[String]) {

  override def apply(credentials: Option[HttpCredentials]): Option[String] =
    credentials.flatMap { cred =>
      if (cred.scheme == "Token" && cred.params.nonEmpty) {
        val tokenStr: String = cred.params.head._2
        tokenService.validateAndGetEmail(tokenStr).toOption
      } else None
    }

}
