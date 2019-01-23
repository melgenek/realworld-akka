package realworld

import akka.http.scaladsl.Http
import akka.http.scaladsl.util.FastFuture._
import com.typesafe.scalalogging.StrictLogging
import realworld.controller.ControllerModule

trait Dependencies extends ControllerModule

class ReadWorld extends Dependencies with StrictLogging {

  import realworld.error.ErrorRejectionHandler.handler

  def run(): Unit = {
    Http().bindAndHandle(routes, httpInterface, httpPort).fast
      .map(binding => logger.info(s"RealWorld server started on ${binding.localAddress}"))
  }

  private val httpInterface: String = config.getString("http.interface")
  private val httpPort: Int = config.getInt("http.port")

}

object ReadWorld extends StrictLogging {

  def main(args: Array[String]): Unit = {
    try {
      val app = new ReadWorld
      app.run()
    } catch {
      case e: Throwable =>
        logger.error("Could not start application", e)
        System.exit(1)
    }
  }

}

