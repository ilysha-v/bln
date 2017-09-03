package BlnService

import akka.actor.ActorSystem
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.query.ScanQuery
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.lang.IgniteBiPredicate

import scala.concurrent.Future
import scala.util.control.NonFatal

class DataAccess(config: IgniteConfig)(implicit system: ActorSystem) {
  implicit val executionContext = system.dispatchers.lookup("ignite-dispatcher")

  if (config.singleMode) Ignition.setClientMode(false)

  private val ignite = Ignition.start(new IgniteConfiguration)
  private val cellIdToCtnCache = {
    val cfg = new CacheConfiguration[CellId, Set[Ctn]]
    cfg.setName("CellIdToCtn")
    cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
    ignite.getOrCreateCache(cfg)
  }
  private val userCache = {
    val cfg = new CacheConfiguration[Ctn, User]
    cfg.setName("User")
    cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
    ignite.getOrCreateCache(cfg)
  }

  def saveUser(user: User): Future[Boolean] = Future {
    userCache.putIfAbsent(user.ctn, user)
  }

  def getCtns(cell: CellId): Future[Option[Set[Ctn]]] = Future {
    Option(cellIdToCtnCache.get(cell))
  }

  def getUsersOnCell(cell: CellId): Future[Option[Set[User]]] = Future {
    import scala.collection.JavaConversions._

    for { // todo тут есть риск рассинхрона между двумя кешами, посмотреть в стороную sql join'a
      links <- Option(cellIdToCtnCache.get(cell))
      users <- Option(userCache.getAll(links).values().toSet)
    } yield users
  }

  def linkCtnWithCell(cell: CellId, ctn: Ctn): Future[Boolean] = Future {
    import collection.JavaConverters._

    val oldCellQuery = new ScanQuery(new IgniteBiPredicate[CellId, Set[Ctn]]() {
      override def apply(e1: CellId, e2: Set[Ctn]) = e2.contains(ctn)
    })

    withTransaction {
      // remove old links
      val oldLinks = cellIdToCtnCache.query(oldCellQuery).getAll.asScala
      assert(oldLinks.size < 2, s"Inconsistent data detected! $ctn linked with more than one cell")
      oldLinks.headOption.fold({}) { entry =>
        cellIdToCtnCache.put(entry.getKey, entry.getValue - ctn)
      }

      Option(userCache.get(ctn)).fold(false) { _ =>
        val current = Option(cellIdToCtnCache.get(cell)).getOrElse(Set.empty)
        cellIdToCtnCache.put(cell, current + ctn)
        true
      }
    }
  }

  private def withTransaction[A](f: => A) = {
    val tx = ignite.transactions().txStart()
    try {
      val result = f
      tx.commit()
      result
    }
    catch {
      case NonFatal(e) =>
        tx.rollback()
        throw e
    }
  }
}
