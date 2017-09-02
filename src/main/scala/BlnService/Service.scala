package BlnService

import akka.http.scaladsl.server.{HttpApp, Route}

object Service extends HttpApp {
  import ApiJsonProtocol._

  val dataAccess = new DataAccess

  override protected def routes: Route =
    pathPrefix("api") {
      path("linkUserToCell") {
        post {
          formFields("ctn".as[Ctn], "cellId".as[CellId]) { (ctn, cellId) =>
            dataAccess.linkWithCell(cellId, ctn)
            complete("okay") // todo
          }
        }
      } ~
      path("connectedUsers") {
        parameters("cellId".as[CellId]) { cellId =>
          rejectEmptyResponse {
            complete(dataAccess.getCtns(cellId))
          }
        }
      }
    }
}