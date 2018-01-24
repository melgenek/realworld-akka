package realworld.util.db

import cats.{Monad, StackSafeMonad}
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

trait DBIOInstances {

  implicit def dbioMonad(implicit ec: ExecutionContext): Monad[DBIO] = new Monad[DBIO] with StackSafeMonad[DBIO] {
    override def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)

    override def pure[A](x: A): DBIO[A] = DBIO.successful(x)
  }

}