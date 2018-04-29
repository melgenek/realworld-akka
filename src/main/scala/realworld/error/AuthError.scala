package realworld.error

abstract class AuthError(val key: String, val message: String)

case class LoginPasswordAuthError() extends AuthError("email or password", "is invalid")

case class TokenError() extends AuthError("token", "no user by token")
