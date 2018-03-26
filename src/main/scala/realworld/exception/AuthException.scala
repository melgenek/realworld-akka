package realworld.exception

abstract class AuthException(val key: String, val message: String)

case class LoginPasswordAuthException() extends AuthException("email or password", "is invalid")

case class TokenException() extends AuthException("token", "no user by token")
