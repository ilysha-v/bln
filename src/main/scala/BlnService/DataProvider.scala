package BlnService

import ignite.scala.ComputeRunner
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.marshaller.jdk.JdkMarshaller

class DataProvider {
//  val cfg = new IgniteConfiguration // configure as appropriate
//  cfg.setClientMode(true)
//  cfg.setMarshaller(new JdkMarshaller)
//  val ignite = Ignition.start(cfg)
//  val compute = ignite.compute(ignite.cluster)
//  implicit val cr = ComputeRunner(compute)
//
//
//  val cache = {
//    val cfg = new CacheConfiguration[String, Int]
//    cfg.setName("Test")
//    ignite.getOrCreateCache(cfg)
//  }
//
//  cache.put("pidor", 42)
//
//  val r = cache.get("pidor")
//  println(r)
}
