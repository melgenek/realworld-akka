package realworld.config

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContextExecutor

trait ActorModule extends ConfigModule {

  implicit lazy val actorSystem: ActorSystem = ActorSystem("realworld", config)

  implicit lazy val actorMaterializer: Materializer = ActorMaterializer()(actorSystem)

  implicit lazy val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

}
