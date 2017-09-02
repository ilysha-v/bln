import BlnService.{Configuration, Service}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object App {
  def main(args: Array[String]): Unit = {
    val config = Configuration()

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer() // todo нужен ли

    implicit val ex: ExecutionContext = system.dispatcher
    new Service(config).startServer("0.0.0.0", config.service.port)
  }
}
