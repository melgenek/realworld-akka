package realworld.util

import org.mockito.stubbing.OngoingStubbing

import scala.concurrent.Future

trait SpecImplicits {

  implicit class OngoingStubbingFuturePimps[T](stubbing: OngoingStubbing[Future[T]]) {
    def thenReturnAsync(value: T): OngoingStubbing[Future[T]] = {
      stubbing.thenReturn(Future.successful(value))
    }
  }

}
