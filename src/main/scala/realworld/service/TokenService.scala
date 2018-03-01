package realworld.service

import com.typesafe.config.Config
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}

import scala.util.Try

trait TokenService {

  def createTokenByEmail(email: String): String

  def validateAndGetEmail(token: String): Try[String]

}

class TokenServiceImpl(config: Config) extends TokenService {

  private val key: String = config.getString("jwt.secret")
  private val issuer: String = config.getString("jwt.issuer")

  def createTokenByEmail(email: String): String =
    Jwts.builder()
      .setIssuer(issuer)
      .setSubject(email)
      .signWith(SignatureAlgorithm.HS512, key)
      .compact()

  def validateAndGetEmail(token: String): Try[String] =
    Try(Jwts.parser().setSigningKey(key).requireIssuer(issuer).parseClaimsJws(token))
      .map(jwt => jwt.getBody.getSubject)

}
