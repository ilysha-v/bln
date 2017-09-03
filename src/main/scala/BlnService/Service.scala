package BlnService

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{HttpApp, Route}

class Service(config: AppConfig)(implicit s: ActorSystem) extends HttpApp {
  import ApiJsonProtocol._

  val dataAccess = new DataAccess(config.ignite)

  override protected def routes: Route =
    path("swagger-ui") {
      getFromResource("swagger/index.html")
    } ~ getFromResourceDirectory("swagger") ~
    pathPrefix("api") {
      path("user") {
        post {
          entity(as[User]) { user =>
            onSuccess(dataAccess.saveUser(user)) { r =>
              if (r) complete("OK")
              else complete(StatusCodes.Conflict)
            }
          }
        }
      } ~
      path("linkUserToCell") {
        post {
          entity(as[CtnWithCellLink]) { link =>
            onSuccess(dataAccess.linkCtnWithCell(link.cellId, link.ctn)) { r =>
              if (r) complete("OK")
              else complete(StatusCodes.NotFound, "User not found")
            }
          }
        }
      } ~
      path("connectedUsers") {
        parameters("cellId".as[CellId]) { cellId =>
          rejectEmptyResponse {
            onSuccess(dataAccess.getUsersOnCell(cellId)) { r =>
              complete(r.map(x => ServiceResponse(x.size, x)))
            }
          }
        }
      } ~
      path("swagger") {
        getFromResource("swagger.yml")
      }
    }
}