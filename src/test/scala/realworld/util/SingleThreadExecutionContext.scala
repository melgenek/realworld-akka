package realworld.util

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object SingleThreadExecutionContext {

  val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(
    Executors.newSingleThreadExecutor()
  )

}
