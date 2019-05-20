package meetup2

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteTcpDiscovery2 extends App {


  val spi = new TcpDiscoverySpi
  val spiMulticast = new TcpDiscoveryMulticastIpFinder
  spiMulticast.setMulticastGroup("228.10.10.170")
  spi.setIpFinder(spiMulticast)

  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(spi)
  cfg.setFailureDetectionTimeout(10000) //Valor por defecto

  val ignite = Ignition.start(cfg)
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(CACHE_NAME)

  println(s"Anuncio10 = ${cacheAnuncios.get("10")}")
  println(s"Anuncio10 = ${cacheAnuncios.get("57")}")
}
