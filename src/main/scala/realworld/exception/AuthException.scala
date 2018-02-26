package realworld.exception

abstract class AuthException(val key: String, val message: String) extends ApiException

object LoginPasswordAuthException extends AuthException("email or password", "is invalid")
