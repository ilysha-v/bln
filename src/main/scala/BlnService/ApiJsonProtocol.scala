package BlnService

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}
import spray.json._

trait ApiJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  def LongAnyValFormat[A](readValue: Long => A, writeValue: A => Long) = new RootJsonFormat[A] {
    override def read(json: JsValue) = json match {
      case JsNumber(n) => readValue(n.toLongExact)
      case _ => deserializationError(s"Unable to parse $json")
    }

    override def write(obj: A) = writeValue(obj).toJson
  }

  implicit val cellIdFormat = LongAnyValFormat[CellId](CellId, _.value)
  implicit val ctnFormat = LongAnyValFormat[Ctn](Ctn, _.value)
}

object ApiJsonProtocol extends ApiJsonProtocol