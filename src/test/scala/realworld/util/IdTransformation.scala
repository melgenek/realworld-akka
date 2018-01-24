package realworld.util

import cats.{Id, ~>}

trait IdTransformation {

  val idTransformation: (Id ~> Id) = new (Id ~> Id) {
    override def apply[A](id: Id[A]): Id[A] = id
  }

}
