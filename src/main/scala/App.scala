import BlnService.{AppConfig, Configuration}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContext

object App {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer() // todo нужен ли

    implicit val ex: ExecutionContext = system.dispatcher

    val route =
      path("api") {
        get {
          complete("OK")
        }
      }

    val config = Configuration()
    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", config.serviceConfig.port) // todo onFailed
    println("Server started")
  }
}
