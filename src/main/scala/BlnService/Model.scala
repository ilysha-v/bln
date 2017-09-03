package BlnService

import java.util.Date

case class CellId(value: Long) extends AnyVal
case class Ctn(value: Long) extends AnyVal
case class CtnWithCellLink(ctn: Ctn, cellId: CellId)
case class User(name: String, email: String, activateDate: Date, ctn: Ctn)

case class ServiceResponse(total: Int, results: Set[User])
