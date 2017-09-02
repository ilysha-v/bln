package BlnService

import java.time.Instant

case class CellId(value: Long) extends AnyVal
case class Ctn(value: Long) extends AnyVal
case class User(name: String, email: String, activateDate: Instant, ctn: Ctn)

case class ServiceResponse(total: Int, results: Seq[User])
