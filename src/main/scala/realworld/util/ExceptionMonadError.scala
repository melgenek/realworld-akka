package realworld.util

import cats.MonadError

import scala.language.higherKinds

object ExceptionME {
  def apply[F[_]](implicit F: MonadError[F, Throwable]): MonadError[F, Throwable] = F
}
