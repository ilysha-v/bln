package BlnService

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{HttpApp, Route}

class Service(config: AppConfig)(implicit s: ActorSystem) extends HttpApp {
  import ApiJsonProtocol._

  val dataAccess = new DataAccess(config.ignite)

  override protected def routes: Route =
    pathPrefix("api") {
      path("user") {
        post {
          entity(as[User]) { user =>
            onSuccess(dataAccess.saveUser(user)) { r =>
              if (r) complete("ok")
              else complete(StatusCodes.Conflict)
            }
          }
        }
      } ~
      path("linkUserToCell") {
        post {
          formFields("ctn".as[Ctn], "cellId".as[CellId]) { (ctn, cellId) =>
            onSuccess(dataAccess.linkWithCell(cellId, ctn)) {
              complete("OK")
            }
          }
        }
      } ~
      path("connectedUsers") {
        parameters("cellId".as[CellId]) { cellId =>
          rejectEmptyResponse {
            onSuccess(dataAccess.getCtns(cellId)) { r =>
              complete(r)
            }
          }
        }
      }
    }
}