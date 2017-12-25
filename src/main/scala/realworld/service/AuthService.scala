package realworld.service

import com.typesafe.config.Config
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import realworld.exception.AuthException

import scala.util.{Failure, Success, Try}

trait AuthService {

  def createTokenByEmail(email: String): String

  def validateAndGetEmail(token: String): Either[AuthException, String]

}

class AuthServiceImpl(config: Config) extends AuthService {

  private val key: String = config.getString("jwt.secret")
  private val issuer: String = config.getString("jwt.issuer")

  def createTokenByEmail(email: String): String =
    Jwts.builder()
      .setIssuer(issuer)
      .setSubject(email)
      .signWith(SignatureAlgorithm.HS512, key)
      .compact()

  def validateAndGetEmail(token: String): Either[AuthException, String] =
    Try(Jwts.parser().setSigningKey(key).requireIssuer(issuer).parseClaimsJws(token)) match {
      case Success(claims) => Right(claims.getBody.getSubject)
      case Failure(e) => Left(AuthException(e))
    }

}
