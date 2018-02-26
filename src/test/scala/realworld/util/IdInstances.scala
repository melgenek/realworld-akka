package realworld.util

import cats.{Id, MonadError, ~>}

import scala.annotation.tailrec

trait IdInstances {

  val idTransformation: (Id ~> Id) = new (Id ~> Id) {
    override def apply[A](id: Id[A]): Id[A] = id
  }

  implicit val idInstances: MonadError[Id, Throwable] = new MonadError[Id, Throwable] {
    override def pure[A](a: A): Id[A] = a

    override def flatMap[A, B](a: Id[A])(f: A => Id[B]): Id[B] = f(a)

    @tailrec def tailRecM[A, B](a: A)(f: A => Either[A, B]): B = f(a) match {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => b
    }

    override def raiseError[A](e: Throwable): Id[A] = throw e

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      try {
        fa
      } catch {
        case e: Throwable => f(e)
      }
  }

}
