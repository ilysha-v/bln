package BlnService

import ignite.scala.ComputeRunner
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.marshaller.jdk.JdkMarshaller

class DataAccess {
  private val cfg = new IgniteConfiguration
  cfg.setMarshaller(new JdkMarshaller)

  private val ignite = Ignition.start(cfg)
  private implicit val runner = ComputeRunner(ignite.compute(ignite.cluster))

  private val cellIdToCtn = {
    val cfg = new CacheConfiguration[CellId, Set[Ctn]]
    cfg.setName("CellIdToCtn")
    cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
    ignite.getOrCreateCache(cfg)
  }

  // todo make async
  def getCtns(cell: CellId): Option[Set[Ctn]] = {
    Option(cellIdToCtn.get(cell))
  }

  // todo по идее когда вяжем новый должны отвязать старый
  def linkWithCell(cell: CellId, ctn: Ctn) = {
    val l = cellIdToCtn.lock(cell)
    l.lock()
    val current = Option(cellIdToCtn.get(cell)).getOrElse(Set.empty)
    cellIdToCtn.put(cell, current + ctn)
    l.unlock()
  }
}
