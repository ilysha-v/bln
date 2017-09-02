import BlnService.Configuration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.{HttpApp, Route}

import scala.concurrent.ExecutionContext

object Service extends HttpApp {
  override protected def routes: Route =
    path("api") {
      path("linkUserToCell") {
        post {
          formFields("ctn".as[Long], "cellId".as[Long]) { (ctn, cellId) =>
            complete("okay")
          }
        }
      }
      path("connectedUsers") {
        get {
          parameters("cellId".as[Long]) { cellId =>
            complete(cellId.toString)
          }
        }
      }
      get {
        complete("OK")
      }
    }
}

object App {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer() // todo нужен ли

    implicit val ex: ExecutionContext = system.dispatcher

    val config = Configuration()
    Service.startServer("0.0.0.0", config.serviceConfig.port)
  }
}
