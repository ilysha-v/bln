package BlnService

case class IgniteConfig(singleMode: Boolean)
case class ServiceConfig(port: Int)
case class AppConfig(service: ServiceConfig, ignite: IgniteConfig)

object Configuration {
  def apply(): AppConfig = {
    import pureconfig._

    loadConfig[AppConfig] match {
      case Left(err) => throw new RuntimeException(s"Configuration error: $err")
      case Right(conf) => conf
    }
  }
}
