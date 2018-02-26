package realworld.rejection

import akka.http.scaladsl.server.Rejection

final case class MissingJsonPropertyRejection(property: String) extends Rejection
