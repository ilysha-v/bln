package BlnService

import java.text.SimpleDateFormat
import java.util.Date

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
  implicit val instantFormat = new RootJsonFormat[Date] {
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override def write(obj: Date) = format.format(obj).toJson

    override def read(json: JsValue) = json match {
      case JsString(stringVal) => format.parse(stringVal)
      case _ => deserializationError("Activate date should be a string")
    }
  }
  implicit val userFormat = jsonFormat4(User)
  implicit val responseFormat = jsonFormat2(ServiceResponse)
}

object ApiJsonProtocol extends ApiJsonProtocol