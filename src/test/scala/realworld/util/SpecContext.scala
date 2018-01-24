package realworld.util

import java.util.concurrent.{LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object SpecContext extends LazyLogging {

  lazy val testExecutionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(
    new ThreadPoolExecutor(
      Runtime.getRuntime.availableProcessors(),
      16,
      1.minute.toMillis,
      TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue[Runnable]()
    )
  )

  sys.addShutdownHook {
    logger.info("Shutting down execution context...")
    testExecutionContext.shutdown()
  }

}

trait SpecContext {

  implicit lazy val testExecutionContext: ExecutionContext = SpecContext.testExecutionContext

}
