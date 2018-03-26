package realworld.auth

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.directives.BasicDirectives.extractExecutionContext
import akka.http.scaladsl.server.directives.{AuthenticationDirective, AuthenticationResult, SecurityDirectives}
import akka.http.scaladsl.util.FastFuture._

import scala.concurrent.Future

class AuthDirectives(emailAuthenticator: EmailAuthenticator) extends SecurityDirectives {

  def authenticate: Directive1[String] = authenticateEmail("realworld")

  private def authenticateEmail(realm: String): AuthenticationDirective[String] =
    extractExecutionContext.flatMap { implicit ec =>
      authenticateOrRejectWithChallenge[String] { cred =>
        emailAuthenticator.andThen(Future.successful)(cred).fast.map {
          case Some(t) ⇒ AuthenticationResult.success(t)
          case None ⇒ AuthenticationResult.failWithChallenge(HttpChallenges.token(realm))
        }
      }
    }

}

object HttpChallenges {

  def token(realm: String): HttpChallenge = HttpChallenge("Token", realm)

}
