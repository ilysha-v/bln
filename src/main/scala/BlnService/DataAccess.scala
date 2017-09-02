package BlnService

import akka.actor.ActorSystem
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}

import scala.concurrent.Future

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

  def getCtns(cell: CellId): Future[Option[Set[Ctn]]] = {
    Future {
      Option(cellIdToCtnCache.get(cell))
    }
  }

  // todo по идее когда вяжем новый должны отвязать старый
  def linkWithCell(cell: CellId, ctn: Ctn): Future[Unit] = {
    Future {
      val l = cellIdToCtnCache.lock(cell)
      l.lock()
      val current = Option(cellIdToCtnCache.get(cell)).getOrElse(Set.empty)
      cellIdToCtnCache.put(cell, current + ctn)
      l.unlock()
    }
  }
}
