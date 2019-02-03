package realworld.util

import cats.Applicative
import cats.data.EitherT
import cats.syntax.either._
import org.mockito.stubbing.OngoingStubbing

import scala.language.higherKinds

trait SpecImplicits {

  implicit class OngoingStubbingEitherT[F[_], L, R](stubbing: OngoingStubbing[EitherT[F, L, R]]) {
    def thenRight(value: R)(implicit a: Applicative[F]): OngoingStubbing[EitherT[F, L, R]] = {
      stubbing.thenReturn(EitherT.fromEither[F](value.asRight[L]))
    }

    def thenLeft(value: L)(implicit a: Applicative[F]): OngoingStubbing[EitherT[F, L, R]] = {
      stubbing.thenReturn(EitherT.fromEither[F](value.asLeft[R]))
    }
  }

}
