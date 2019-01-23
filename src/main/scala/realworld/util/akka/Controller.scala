package realworld.util.akka

import akka.http.scaladsl.server.{Directives, Route}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport

trait Controller extends Directives with ErrorAccumulatingCirceSupport {

  def route: Route

}
