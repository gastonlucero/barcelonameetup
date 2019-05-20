package meetup2

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteTcpDiscovery2 extends App with IgniteTcpDiscovery {

  //Discovery entre los especificando explicitamente TcpDiscoverySpi cambiando la ip de descubrimiento

  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(tcpSpiConfig())
  cfg.setFailureDetectionTimeout(10000) //Valor por defecto

  val ignite = Ignition.start(cfg)
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(CACHE_NAME)

  println(s"Anuncio10 = ${cacheAnuncios.get("10")}")
  println(s"Anuncio10 = ${cacheAnuncios.get("57")}")
}
