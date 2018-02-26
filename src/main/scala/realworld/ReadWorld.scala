package realworld

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.util.FastFuture._
import com.typesafe.scalalogging.StrictLogging
import realworld.controller.ControllerModule
import realworld.validation.ValidationExceptionHandler

object ReadWorld extends Dependencies with StrictLogging {

  implicit val exceptionHandler: ExceptionHandler = ValidationExceptionHandler.handler

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(routes, httpInterface, httpPort).fast
      .map(binding => logger.info(s"RealWorld server started on ${binding.localAddress}"))
  }

  lazy val httpInterface: String = config.getString("http.interface")
  lazy val httpPort: Int = config.getInt("http.port")

}

trait Dependencies extends ControllerModule
