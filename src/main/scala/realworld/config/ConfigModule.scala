package realworld.config

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigModule {

  lazy val config: Config = ConfigFactory.load()

}
