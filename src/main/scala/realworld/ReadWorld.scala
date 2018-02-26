package realworld

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.http.scaladsl.util.FastFuture._
import com.typesafe.scalalogging.StrictLogging
import realworld.controller.ControllerModule
import realworld.exception.handler.{AuthExceptionHandler, ValidationExceptionHandler}
import realworld.rejection.handler.ValidationRejectionHandler

object ReadWorld extends Dependencies with StrictLogging {

  implicit val exceptionHandler: ExceptionHandler =
    ValidationExceptionHandler.handler.withFallback(AuthExceptionHandler.handler)

  implicit val rejectionHandler: RejectionHandler = ValidationRejectionHandler.handler

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(routes, httpInterface, httpPort).fast
      .map(binding => logger.info(s"RealWorld server started on ${binding.localAddress}"))
  }

  lazy val httpInterface: String = config.getString("http.interface")
  lazy val httpPort: Int = config.getInt("http.port")

}

trait Dependencies extends ControllerModule
