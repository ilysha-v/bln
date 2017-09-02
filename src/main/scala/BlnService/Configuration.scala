package BlnService

import com.typesafe.config.ConfigFactory

case class ServiceConfig(port: Int)
case class AppConfig(serviceConfig: ServiceConfig)

object Configuration {
  def apply(): AppConfig = {
    import pureconfig._

    loadConfig[AppConfig] match {
      case Left(err) => throw new RuntimeException(s"Configuration error: $err")
      case Right(conf) => conf
    }
  }
}
