package realworld.exception

case class AuthException(e: Throwable) extends RuntimeException(e)
