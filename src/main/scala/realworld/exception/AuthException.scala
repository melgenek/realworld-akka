package realworld.exception

abstract class AuthException(val key: String, val message: String) extends ApiException

object LoginPasswordAuthException extends AuthException("email or password", "is invalid")

object TokenException extends AuthException("token", "no user by token")
